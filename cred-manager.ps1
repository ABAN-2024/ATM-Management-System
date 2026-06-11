<#
cred-manager.ps1
PowerShell helper to save/get/remove ATM DB credentials in Windows Credential Manager.
Depends on the `CredentialManager` module from PSGallery.
#>

function Ensure-CredModule {
    if (-not (Get-Module -ListAvailable -Name CredentialManager)) {
        Write-Host "CredentialManager module not found. Installing from PSGallery..."
        try {
            Install-Module -Name CredentialManager -Scope CurrentUser -Force -Confirm:$false -ErrorAction Stop
        } catch {
            Write-Error "Failed to install CredentialManager module: $_"
            return $false
        }
    }
    return $true
}

function Save-AtmCredential {
    [CmdletBinding()]
    param(
        [Parameter(Mandatory=$true)][string]$Username,
        [Parameter(Mandatory=$true)][string]$Password,
        [string]$Target = "ATM_Database"
    )
    if (-not (Ensure-CredModule)) { return }
    New-StoredCredential -Target $Target -UserName $Username -Password $Password -Persist LocalMachine | Out-Null
    Write-Host "Saved credential to Windows Credential Manager (Target='$Target')."
}

function Get-AtmCredential {
    [CmdletBinding()]
    param(
        [string]$Target = "ATM_Database"
    )
    if (-not (Ensure-CredModule)) { return $null }
    $c = Get-StoredCredential -Target $Target
    if ($null -eq $c) {
        Write-Host "No credential found for target='$Target'."
        return $null
    }
    # return a simple hashtable with username & password
    return @{ UserName = $c.UserName; Password = $c.GetNetworkCredential().Password }
}

function Remove-AtmCredential {
    [CmdletBinding()]
    param(
        [string]$Target = "ATM_Database"
    )
    if (-not (Ensure-CredModule)) { return }
    Remove-StoredCredential -Target $Target
    Write-Host "Removed credential for Target='$Target'."
}

<# Examples:
# Save credentials interactively:
Save-AtmCredential -Username "atm_user" -Password "S3cureP@ssw0rd!"

# Retrieve credentials:
$creds = Get-AtmCredential
if ($creds) { $creds.UserName; $creds.Password }

# Remove:
Remove-AtmCredential
#>

# ATM-Management-System

## Secure local environment (recommended)

Use a local `.env` file for development secrets and do NOT commit it. Steps:

1. Copy `.env.example` to `.env` and fill in the password.

2. Run the project with the included PowerShell runner so credentials are process-scoped:

```powershell
# from repository root
.\run-with-env.ps1
```

This avoids persisting plaintext credentials in your Windows user environment (no `setx`). The `.env` file is ignored by Git via `.gitignore`.

### Optional: use Windows Credential Manager

You can store the DB username/password securely in Windows Credential Manager and retrieve them at runtime instead of keeping them in `.env`.

1. Load the helper (installs module if needed):

```powershell
Import-Module .\cred-manager.ps1
```

2. Save credentials (one-time):

```powershell
Save-AtmCredential -Username "atm_user" -Password "S3cureP@ssw0rd!"
```

3. Retrieve in PowerShell:

```powershell
$creds = Get-AtmCredential
$creds.UserName; $creds.Password
```

The helper script `cred-manager.ps1` is included in the repo and will install the `CredentialManager` module automatically when first used.

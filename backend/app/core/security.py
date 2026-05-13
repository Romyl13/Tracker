"""
Firebase ID token verification stub.

Production: verify JWT against Google JWKS for your Firebase project
(https://www.googleapis.com/service_accounts/v1/jwk/securetoken@system.gserviceaccount.com)
or use firebase-admin SDK.

Here we accept a header for local development and extract a stable user id substring.
"""

from fastapi import Header, HTTPException


async def get_current_user_id(authorization: str | None = Header(default=None)) -> str:
    if not authorization or not authorization.lower().startswith("bearer "):
        raise HTTPException(status_code=401, detail="Missing or invalid Authorization header")
    token = authorization.split(" ", 1)[1].strip()
    if len(token) < 8:
        raise HTTPException(status_code=401, detail="Invalid token")
    # Demo mapping: use token prefix as pseudo user id (replace with real Firebase verify)
    return f"fb_{token[:32]}"

# Krav — Team Adapter

## Bakgrund

Adaptern kapslar in anrop till team-tjänsten och exponerar dess endpoints
som ett typat Java-API för konsumenter inom Rimfrost.

---

## Funktionella krav

### TEAM-FR-01 — Hämta team för individ

- **TEAM-FR-01.1** Adaptern ska returnera en lista med team för angiven individ när tjänsten svarar med HTTP 200.

### TEAM-FR-02 — Hämta individer för team

- **TEAM-FR-02.1** Adaptern ska returnera en lista med individer för angivet team när tjänsten svarar med HTTP 200.

### TEAM-FR-03 — Felhantering

- **TEAM-FR-03.1** Om tjänsten svarar med HTTP 404 ska adaptern kasta ett `TeamException` med `ErrorType.NOT_FOUND`.
- **TEAM-FR-03.2** Om tjänsten svarar med HTTP 400 ska adaptern kasta ett `TeamException` med `ErrorType.BAD_REQUEST`.
- **TEAM-FR-03.3** Om tjänsten svarar med HTTP 503 ska adaptern kasta ett `TeamException` med `ErrorType.SERVICE_UNAVAILABLE`.
- **TEAM-FR-03.4** Vid övriga HTTP-fel eller kommunikationsfel ska adaptern kasta ett `TeamException` med `ErrorType.UNEXPECTED_ERROR`.

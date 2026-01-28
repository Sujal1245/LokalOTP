# LokalOTP

## Introduction
This project was made as an assignment for the Android Intern role at Lokal.

## Setup Instructions
Open the project in Android Studio and use a device/emulator running Android Nougat or higher to build and run the app.

## OTP Logic
The OTP is generated through the `OtpManager` by first generating a random number in the range **[0, 100000)** and then left-padding it with `'0'` characters to ensure a fixed 6-digit format.  
For example, the number `45` becomes `"000045"`.

## Expiry Handling
The model used to define an `OtpEntry` encapsulates a `generatedAtMillis` field.  
This value is compared against `currentTimeMillis` inside `OtpManager` whenever OTP validation is requested, allowing expiry checks to be handled centrally.

## Data Structure Used
To map emails to their respective **valid OTPs**, Kotlin’s `MutableMap<String, OtpEntry>` is used.  
This keeps the implementation simple and ensures that the private `otpStore` acts as a **single source of truth** for all OTP-related data.

## External SDK
Timber was used because the scope of the app is limited and it fits well with the minimal requirement of logging messages only in **debug mode**.  
The library is lightweight, easy to set up, and allows logging without introducing unnecessary complexity or external dependencies.

## Use of GPT

GPT was used to create a rough initial model of the application, including early versions of screens, the ViewModel, and the OTP manager. Throughout this process, GPT was guided by architectural decisions made at various points, such as how states should be defined, the use of channels for one-time events, and the appropriate location for state hoisting.

After generating the initial code, it was continuously aligned with the business rules and requirements of the assignment through multiple refactor-and-test cycles until a satisfactory and correct implementation was achieved.

### Deviations from GPT-Generated Code

In several areas, the GPT-generated code was intentionally replaced with custom implementations due to architectural or behavioral concerns. Some notable examples include:

1. Resetting state using keys in `remember` blocks
2. Introducing proper loading phases for buttons
3. Refining OTP expiry logic
4. Using explicit event-based navigation instead of implicit state-driven jumps

## Known Limitations (Scope for Improvement)

1. **Shared ViewModel Scope**  
   Currently, a single `AuthViewModel` is shared across the application. Ideally, responsibilities should be divided per screen, with each screen having its own ViewModel and dedicated UI state data class to encourage better separation of concerns.  
   Introducing an `AuthRepository` in a domain layer could further improve the architecture, but this would be an overkill given the small scope of the assignment.

2. **Local OTP Generation**  
   OTPs are generated locally, and therefore failure scenarios or error handling related to network calls are not considered. In a real-world application, OTP generation and validation would typically involve remote APIs and corresponding error handling.

3. **Session Persistence**  
   The user session does not survive process death and is not persisted across app restarts. A production-ready implementation would require persisting session state using an appropriate storage mechanism.

4. **Transient Nav States**
   Transient navigation states are not handled in a fully robust manner.

# LockTalk: Secure Messaging App

**Contributors**:
- Tarnveer Takhtar [@takhtart](https://github.com/takhtart)
- Alex Damjano [@alexdamjano](https://github.com/alexdamjano)
- Aryan Patel [@Aryanpatel335](https://github.com/Aryanpatel335)
- Akram Hannoufa [@ak-hannou](https://github.com/ak-hannou)

## Concept Overview

The LockTalk App is part of a project for a Software Design course tasked with creating a secure messaging platform to be installed on company issued android phones with a focus on addressing the communication needs of organizations dealing with sensitive information.

### Key Features

1. **Key Distribution Centre (KDC) Server**: Central to LockTalk's concept is the implementation of a Key Distribution Centre server, facilitating the dynamic generation and distribution of encryption keys for secure communication sessions.

2. **Mediated Authentication Protocol (Kerberos)**: LockTalk integrates a custom Kerberos inspired ticket system, that continuosly refreshes keys in the backend and checks for authorization prior to accessing a chat log.

3. **Symmetric-Key Crypto-System (Blowfish)**: Utilizing Blowfish as the symmetric-key encryption algorithm, LockTalk constantly encrypts and decrypts messages to and from the server, ensuring secure transmission of messages to the server.

4. **Secure Chat History Logging**: LockTalk securely stores chat history logs in a Firestore Database, including identifiers of communicating agents, timestamps, and chat transcripts (encrypted).

5. **Screenshot Prevention and Reporting**: LockTalk incorporates mechanisms to prevent unauthorized screenshots within the application and provides reporting functionality to notify admins of any potential breaches of information.

6. **Notifications**: Leveraging Firebase Cloud Messaging (FCM) and OkHttp, LockTalk delivers notifications to users via FCM token.

7. **Geofencing**: LockTalk utilizes geofencing technology to define virtual boundaries, enabling organizations to enforce location-based restrictions on communication activities for enhanced security.

8. **Employee Directory Integration**: LockTalk features a searchable company directory, allowing users to initiate conversations directly from the application. This integration streamlines communication workflows and ensures that communications are initiated exclusively between authorized employees.

---

**Note**: While LockTalk is presented as a conceptual framework, its realization would require further development, validation, and refinement to ensure compatibility with Android devices and adherence to industry standards and best practices.

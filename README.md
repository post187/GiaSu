Tutoring Marketplace Platform

A full-stack tutoring marketplace platform that connects Students and Tutors, supporting:

Class publishing

Booking & scheduling

Session lifecycle tracking

Escrow-based payment system

Trust score & verification system

Notification & reminder engine

🏗 System Architecture

The system is designed using a domain-driven structure, separating responsibilities into clear business domains:

User & Profile Management

Class & Scheduling

Booking Lifecycle

Session Lifecycle

Escrow & Ledger (Financial tracking)

Notification & Reminder System

Review & Trust System

🗂 Database ER Diagram

Below is the high-level Entity Relationship Diagram of the system.
erDiagram

    User {
        string id PK
        string fullName
        string email
        string role
        string status
    }

    StudentProfile {
        string id PK
        string userId FK
        string gradeLevel
    }

    TutorProfile {
        string id PK
        string userId FK
        string verificationStatus
        float trustScore
    }

    Subject {
        string id PK
        string name
        string level
    }

    Class {
        string id PK
        string tutorId FK
        string subjectId FK
        string status
        string lifecycleStatus
    }

    ClassSchedule {
        string id PK
        string classId FK
        json recurrenceRule
    }

    Session {
        string id PK
        string classId FK
        datetime scheduledStartAt
        string status
    }

    Booking {
        string id PK
        string classId FK
        string studentId FK
        string tutorId FK
        string status
    }

    PaymentIntent {
        string id PK
        string classId FK
        string payerId FK
        float amount
        string status
    }

    EscrowAccount {
        string id PK
        string classId FK
        float totalDeposited
        float availableBalance
    }

    LedgerEntry {
        string id PK
        string classId FK
        string sessionId FK
        string type
        float amount
    }

    Review {
        string id PK
        string bookingId FK
        int rating
    }

    Notification {
        string id PK
        string userId FK
        string channel
    }

    ReminderLog {
        string id PK
        string userId FK
        string sessionId FK
        string reminderType
    }

    User ||--o| StudentProfile : has
    User ||--o| TutorProfile : has
    User ||--o{ Notification : receives
    User ||--o{ ReminderLog : receives
    User ||--o{ PaymentIntent : pays

    TutorProfile ||--o{ Class : creates
    Subject ||--o{ Class : categorizes

    Class ||--|| ClassSchedule : has
    Class ||--o{ Session : contains
    Class ||--o{ Booking : has
    Class ||--|| EscrowAccount : owns
    Class ||--o{ LedgerEntry : records
    Class ||--o{ PaymentIntent : receives

    Booking ||--|| Review : generates

    Session ||--o{ ReminderLog : triggers
    Session ||--o{ LedgerEntry : releases

## Business Flow Overview
Student Booking Flow

Student views published Class

Create Booking

Tutor confirms

Sessions generated

Payment deposited

Sessions conducted

Funds released after confirmation

Student leaves Review

## Design Principles

Explicit lifecycle management (Class, Booking, Session)

Immutable financial tracking (LedgerEntry)

Strong data integrity via:

Unique constraints

Foreign keys

Indexing

Separation of concerns per domain

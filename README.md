# Last Value Price Service

## Overview
An in-memory, thread-safe service for storing and retrieving the latest price
of financial instruments based on `asOf` timestamp.

## Features
- Atomic batch visibility
- Lock-free reads
- Batch lifecycle enforcement
- Resilient to incorrect producer usage
- JVM-local API (no REST, no DB)

## Design Highlights
- Copy-on-write snapshots using AtomicReference
- Staging area per batch
- Latest-asOf-wins strategy
- Thread-safe without read locks

## Why In-Memory?
Chosen to match assignment requirements and demonstrate concurrency control
without relying on databases.

## Possible Improvements
- Persistence (DB / Kafka)
- Versioned snapshots
- Backpressure on batch ingestion

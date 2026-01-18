package ru.practicum.shareit.booking;

public enum BookingState {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,   // фильтрует по статусу WAITING
    REJECTED   // фильтрует по статусу REJECTED
}

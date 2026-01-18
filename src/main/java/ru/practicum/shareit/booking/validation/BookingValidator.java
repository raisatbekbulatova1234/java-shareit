package ru.practicum.shareit.booking.validation;

import ru.practicum.shareit.booking.model.Booking;

public interface BookingValidator {

    void validateCreateBooking(Booking booking);

    void validateApprovalRights(Long bookingId, Long ownerId);

    void validateAccessToBooking(Long bookingId, Long userId);
}

package ru.practicum.shareit.booking.model;

import lombok.Data;

@Data
public class BookingPair {
    private Booking lastBooking;    // последнее завершившееся или текущее
    private Booking nextBooking;   // следующее (ещё не начавшееся)


    public BookingPair(Booking lastBooking, Booking nextBooking) {
        this.lastBooking = lastBooking;
        this.nextBooking = nextBooking;
    }
}


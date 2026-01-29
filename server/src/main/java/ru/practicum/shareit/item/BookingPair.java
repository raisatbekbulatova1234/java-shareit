package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.model.Booking;

@Getter
@Setter
@AllArgsConstructor
public class BookingPair {
    private Booking lastBooking;
    private Booking nextBooking;

}

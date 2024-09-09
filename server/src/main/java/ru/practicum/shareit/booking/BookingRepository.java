package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {
    List<Booking> findAllByBookerId(int bookerId);

    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(int bookerId, LocalDateTime cur, LocalDateTime cur2);

    List<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(int bookerId, LocalDateTime cur);

    List<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(int bookerId, LocalDateTime cur);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(int bookerId, BookingStatus status);

    List<Booking> findAllByItemOwnerIdOrderByStartDesc(int ownerId);

    List<Booking> findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(int ownerId, LocalDateTime cur, LocalDateTime cur2);

    List<Booking> findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(int ownerId, LocalDateTime cur);

    List<Booking> findAllByItemOwnerIdAndStartAfterOrderByStartDesc(int ownerId, LocalDateTime cur);

    List<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(int ownerId, BookingStatus status);

    List<Booking> findAllByBookerIdAndItemId(int bookerId, int itemId);

    Optional<Booking> findFirstByItemIdAndStartBeforeAndStatusNotOrderByStartDesc(int itemId, LocalDateTime cur, BookingStatus status);

    Optional<Booking> findFirstByItemIdAndStartAfterAndStatusNotOrderByStart(int itemId, LocalDateTime cur, BookingStatus status);

}

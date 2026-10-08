# ✈️ OOP Flight System

A desktop airline reservation system built in **Java 17** with a **Swing** GUI. The project focuses on clean object-oriented design and on demonstrating a real concurrency problem: a multithreaded seat-booking simulation that reproduces a race condition and then fixes it with synchronization.

---

## 🚀 Features

**For passengers**
- Register and log in (role-based: `ADMIN` / user)
- Search flights by departure and arrival city; past-dated flights are filtered out automatically
- Pick a seat from an interactive seat map with **Business** (rows 1–5) and **Economy** classes
- Create and cancel reservations, each identified by a generated PNR code

**For administrators**
- Flight management: list, add, edit, and delete flights (with date and time pickers)
- Staff management: add and remove staff members
- System simulation panel for concurrency and background-task tests

**Persistence**
- All data is stored in plain-text CSV files (`users.txt`, `flights.txt`, `reservations.txt`, `staff.txt`) and reloaded at startup

---

## 🧵 Concurrency Simulation

The admin panel includes a simulation where **90 threads** try to book random seats on a 180-seat aircraft at the same time.

| Mode | What happens | Result |
|------|--------------|--------|
| **Unsynchronized** | Each thread picks a free seat, waits 20 ms, then marks it as taken. During that window other threads pick the *same* seat (a classic check-then-act race condition). | Fewer than 90 seats end up occupied |
| **Synchronized** | Finding and reserving a seat happens inside a `synchronized` method, so only one thread can perform the operation at a time. | Exactly 90 seats occupied |

A separate **asynchronous report generator** (`ReportGenerator implements Runnable`) computes occupancy statistics on a background thread so the UI never freezes. Results are pushed back to the interface with `SwingUtilities.invokeLater`, because Swing components must only be updated from the Event Dispatch Thread.

---

## 🧱 Architecture

The code is split into layers so that the GUI never manipulates data directly; it talks to manager classes instead.

```
src/
├── flight_management/       # Core domain model
│   ├── Flight, Route, Plane, Seat
│   ├── SeatClass (enum with price multipliers)
│   └── User, Staff
├── reservation_ticketing/   # Booking domain
│   └── Reservation, Passenger, Ticket, Baggage
├── services_managers/       # Business logic & file persistence
│   ├── FlightManager, ReservationManager, SeatManager
│   ├── UserManager, StaffManager
│   ├── CalculatePrice
│   └── ReportGenerator (background thread)
├── gui/                     # Swing user interface
│   ├── MainFrame (CardLayout navigation)
│   ├── LoginPanel, RegisterDialog, SearchPanel
│   ├── SeatSelectionDialog, UserReservationsPanel
│   └── AdminPanel, ReservationManagementPanel, SimulationPanel
└── MainTest/                # JUnit 5 tests & concurrency test runner
```

### OOP principles in the code

- **Encapsulation:** all fields are private and accessed through methods; for example, a seat's reservation state lives inside `Seat`.
- **Composition:** a `Flight` has a `Route` and a `Plane`; a `Plane` owns a 2D `Seat` matrix; a `Reservation` links a `Flight`, a `Passenger`, and a `Seat`.
- **Enums with behavior:** `SeatClass` carries its own price multiplier (`BUSINESS = 2.5×`), avoiding if-else chains in pricing.
- **Inheritance:** GUI components extend Swing classes (`JFrame`, `JPanel`, `JDialog`).
- **Polymorphism:** `ReportGenerator` is run through the `Runnable` interface (runtime polymorphism); `ReservationManager.createReservation` is overloaded (compile-time polymorphism); `toString()` is overridden across model classes.

---

## 🧪 Tests

- **`ProjectTests`** (JUnit 5): price calculation, route-based flight search, filtering of past flights, seat count decreasing after reservation, and exception handling for invalid seat numbers.
- **`ReservationManagerTest`**: a console runner that executes the synchronized and unsynchronized booking scenarios and the asynchronous report task, printing a seat map for each.

---

## 💻 Getting Started

**Requirements:** JDK 17+

**Run from an IDE (Eclipse / IntelliJ):** open the project and run `gui.MainFrame`.

**Run from the terminal:**
```bash
git clone https://github.com/emir-utku-ozgen/OOP-Flight-System.git
cd OOP-Flight-System
mkdir -p bin
javac -d bin $(find src -name "*.java" -not -path "*/MainTest/*")
java -cp bin gui.MainFrame
```

---

## 🔭 Possible Improvements

- Move from a coarse `synchronized` method to finer-grained locking (per flight or per seat, e.g. `AtomicBoolean.compareAndSet`) and apply it to the regular booking flow as well
- Replace the role string with a `Role` enum or a `User` class hierarchy
- Hash passwords instead of storing them in plain text
- Inject manager dependencies through constructors to improve testability
- Replace CSV files with a database (e.g. SQLite)

# ParkWatch — Product Flow

## Main Goal

Allow the user to save and retrieve the location of their parked vehicle quickly from a Wear OS smartwatch.

---

## Main User Flow

### No saved parking location

Home
→ Save Vehicle
→ Select Floor
→ Select Zone
→ Select Row
→ Enter Parking Spot (optional)
→ Review
→ Save
→ Confirmation
→ Home

---

### Saved parking location

Home
→ View Vehicle
→ Parking Detail

Parking Detail allows:

- View floor
- View zone
- View row
- View parking spot
- View parking time
- Edit location
- Delete location

---

## Edit Flow

Parking Detail
→ Edit
→ Update parking information
→ Save
→ Confirmation
→ Parking Detail

---

## Delete Flow

Parking Detail
→ Delete
→ Delete Confirmation

If confirmed:

Delete saved location
→ Home empty state

If cancelled:

Return to Parking Detail

---

## Voice Flow

Home or Save Parking
→ Voice Input
→ User speaks parking information
→ Parsed parking information
→ Review
→ Save

Example:

"Piso 2, zona B, fila 14, puesto 27"

Result:

Floor: 2
Zone: B
Row: 14
Spot: 27

---

## Location Flow

GPS location is optional.

Save Parking
→ Request Location Permission
→ Capture current location

If permission is denied:

Continue without GPS.

Manual parking information must always remain available.

---

## Home States

### Empty

Display:

Where did you park?

Primary action:

Save vehicle

Secondary action:

Voice input

---

### Vehicle Saved

Display:

Floor

Zone

Row

Parking spot if available

Time since parking

Primary action:

View vehicle

---

## Core Requirement

ParkWatch must work without an Internet connection.

The following features must work offline:

- Save parking location
- View parking location
- Edit parking location
- Delete parking location
- View parking time

GPS and voice features enhance the experience but must not block manual usage.
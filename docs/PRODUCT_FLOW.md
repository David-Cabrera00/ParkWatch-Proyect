# ParkWatch — Product Flow

## Main Goal

Allow the user to save and retrieve the location of their parked vehicle quickly from a Wear OS smartwatch.

---

## Main User Flow

### No saved parking location

Home offers 3 options to save:
1. **Manual Wizard**: Home → Save Vehicle → Floor → Zone → Row → Spot (optional) → Review → Save
2. **Quick GPS Save (1-Tap)**: Home → Quick save (GPS) → Instantly captures coordinates & timestamp → Detail
3. **Voice Input**: Home → Voice input → Spoken text parsed → Review → Save

---

### Saved parking location

Home
→ View Vehicle
→ Parking Detail

Parking Detail allows:

- View floor, zone, row, spot
- View parking elapsed time
- View / set / edit / remove parking timer (parquímetro)
- View on map (if GPS coordinates are available)
- Share parking location (SMS, WhatsApp, companion phone via Android Share Sheet)
- Edit location
- Delete location (with explicit confirmation)

---

## Quick GPS Save Flow (1-Tap)

Home (Empty state)
→ Tap "Quick save (GPS)"
→ If permission needed, requests location permission
→ Captures current GPS coordinates & current timestamp
→ Saves location immediately with floor="GPS"
→ Opens Parking Detail (user can tap "Edit" later to add floor/zone/spot)

---

## Share Location Flow

Parking Detail
→ Tap "Share location"
→ Opens system Share Chooser (`Intent.ACTION_SEND`)
→ Formats location details + Google Maps link:
  > "Mi vehículo en ParkWatch:
  > Piso: P2
  > Zona: B
  > Fila: 14
  > Puesto: 27
  > https://maps.google.com/?q=..."

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

Delete saved location & cancel any active timer
→ Home empty state

If cancelled:

Return to Parking Detail

---

## Parking Timer Flow (Parquímetro)

Parking Detail
→ Set Time Limit
→ Select duration (15m, 30m, 45m, 1h, 1.5h, 2h, 3h)
→ Save timer
→ Return to Parking Detail (displays active countdown & limit time)

When timer is active:
- 15 minutes before limit: High-priority Wear OS warning notification + vibration
- At expiration: High-priority Wear OS expiration notification + vibration
- Home screen and Detail screen show remaining time badge

Options from Parking Detail:
- Modify time limit
- Remove time limit

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

Save vehicle (Wizard)

Secondary actions:

- Quick save (GPS)
- Voice input
- Settings

---

### Vehicle Saved

Display:

Floor

Zone

Row

Parking spot if available

Time since parking

Active timer / limit countdown (if set)

Primary action:

View vehicle

Secondary action:

Settings

---

## Core Requirement

ParkWatch must work without an Internet connection.

The following features must work offline:

- Save parking location (manual, quick GPS, or voice)
- View parking location
- Edit parking location
- Delete parking location
- View parking time
- Set & receive parking timer alerts
- Share parking location text

GPS and voice features enhance the experience but must not block manual usage.

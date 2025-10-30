# MEDLABS - Agent-Based Simulation for Disease Spread in Cities and Regions

## 3.6. Input files: Locations

The locations are provided in a gzipped csv-file. The total file is typically large: tens of thousands to hundreds of thousands of locations, since each household is included separately.

The file has the following columns:

- `location_id`: the unique number, used in the people file for home location, work location and school location.
- `nb_sublocations`: number of sub-locations (offices, school classes, shops in a mall, etc.).
- `location_category`: the location type name from the location type file.
- `lat`: the latitude.
- `lon`: the longitude.
- `area`: The area in m2.

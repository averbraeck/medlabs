# MEDLABS - Agent-Based Simulation for Disease Spread in Cities and Regions

## 3.4. Input files: People

The people file is a synthetic population with a person type and socio-demographic properties. The calibration of such a file based on statistical data is described in: Ge, Y., R. Meng, Z. Cao, X. Qiu and K. Huang (2014). Virtual city: An individual-based digital environment for human mobility and interactive behavior. Simulation, 90(8), pp. 917–935. https://doi.org/10.1177/0037549714531061

The artificial city file is a gzipped csv-file with the following columns:

- `person_id`: The unique identifier (integer) of a person.
- `household_id`: The identifier of a household to keep persons of the same household together in the same house.
- `age`: age at the start of the simulation.
- `home_id`: the id of the location (home) where the person lives.
- `workplace_id`: the id of the location where the person works or goes to school.
- `social_role`: the person type for which an activity schedule exists.

The gender is currently drawn from a distribution with $p=0.5$, but this can also be read from the file if needed (class `ConstructHerosModel`, method `readPersonTable()`). 

Since there are so many persons in a simulation, the number of fields is kept to the bare minimum.


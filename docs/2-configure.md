# MEDLABS
## Agent-Based Simulation for Disease Spread in Cities and Regions

## 2. Configuration files

### 2.1. Main properties file

The main properties file looks, e.g., as follows:

```
# path for the input files
generic.InputPath = ./data

# write output files?
generic.WriteOutput = true

# output path for the output files (will be created if it doesn't exist)
generic.OutputPath = ./out

# run length in days
generic.RunLength = 60

# seed for the RNG
generic.Seed = 111

# interval for dumping person data, no dump if value = 0
generic.PersonDumpIntervalDays = 0


# SETTINGS FOR BUILDING THE MODEL

# number of persons in the model 
settings.NumberPersons = 1000

# number of workplaces in the model 
settings.NumberWorkplaces = 10

# number of sublocations for each workplace
settings.NumberSublocations = 10

# workplace size in m2 (for 100 people)
settings.WorkplaceSize = 1000.0

# number of people infected at t=0
settings.NumberInfected = 1


# SEIR TRANSMISSION MODEL PARAMETERS

# base contagiousness parameter
SEIR.contagiousness = 0.5

# initial variable for personal protection factor, to be multiplied with base contagiousness
SEIR.beta = 1.0

# rough calculation constant to indicate first day of contagiousness of an exposed person (days)
SEIR.t_e_min = 2.0

# rough calculation constant to indicate peak day of contagiousness of an exposed person (days)
SEIR.t_e_mode = 7.0

# rough calculation constant to indicate last day of contagiousness of an exposed person (days)
SEIR.t_e_max = 12.0

# threshold for the transmission calculation. Set to 1 minute (seconds)
SEIR.calculation_threshold = 60
```

The following parameters are key:

- `generic.InputPath` indicates where the other files (except the output path) can be found. In the above example, the folder is `./data`. This means that the file paths for people, locations, activities, etc. can all be found in the folder `./data`, where the `./` is relative to the location of the jar file that is being executed. Note that the SEIR demo model does not use any data at the moment.
- `generic.PersonDumpIntervalDays` does not dump all persons in the model with all their properties. When the interval is set at a N days, all persons in the model dump their state to a file every N days. This csv file can grow very large, therefore the default value is 0.
- All other parameters are explained in the comments above the parameter.
- The input files for people, locations, activities, etc. are discussed in the [input files](3-input.md) document.


### 2.2. Use of the main properties file

Multiple instances of a main properties file can exist, and these files can be in multiple directories. Typically, they are placed in the same directory as the jar file that is run to execute the model. When two files, `lockdown.properties` and `normal.properties` exist, each with their own parameters and files on which they are dependent, they can be run with:

```
java -jar medlabs-heros-full-2.1.4.jar /normal.properties
```

or

```
java -jar medlabs-heros-full-2.1.4.jar /lockdown.properties
```

Without the `batch` argument, both of the above runs of the application would be in interactive mode.

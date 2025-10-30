# MEDLABS - Agent-Based Simulation for Disease Spread in Cities and Regions

## 2. Configuration files

### 2.1. Main properties file

The main properties file looks, e.g., as follows:

```
# path for the input files
generic.InputPath = /data/thehague

# path for the person file (blank means standard people.csv.gz file under generic.InputPath) 
generic.PersonFilePath = people/people.csv.gz

# path for the locations file (blank means standard locations.csv.gz file under generic.InputPath)
generic.LocationsFilePath = locations/locations.csv.gz

# path for the locationtypes file (blank means standard locationtypes.csv file under generic.InputPath)
generic.LocationTypesFilePath = locations/locationtypes.csv

# path for the activitypatterns file (blank means standard activityschedules.xlsx file under generic.InputPath)
generic.ActivityFilePath = activities/activityschedules_cap.xlsx

# path for the probability based infection ratios for locations such as satellite cities
generic.ProbRatioFilePath = epidemiology/infection_rates.csv
 
# path for the csv control file for the map (animation - only loaded in interactive mode)
generic.osmControlFile = locations/thehague.osm.csv

# path for the osm map file (animation - only loaded in interactive mode)
generic.osmMapFile = locations/haaglanden.osm.pbf

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

# number of people infected at t=0
policies.NumberInfected = 100

# lowest age of people infected at t=0
policies.MinAgeInfected = 0

# highest age of people infected at t=0
policies.MaxAgeInfected = 100

# policy file for locations (can be blank)
policies.LocationPolicyFile = 

# policy file for disease parameters (can be blank)
policies.DiseasePolicyFile = 


# INCLUDE FILE FOR DISEASE
generic.diseasePropertiesFile = /alpha-distance.properties

# whether the model is area based or distance based
generic.diseasePropertiesModel = distance
```

The following parameters are key:

- `generic.InputPath` indicates where the other files (except `generic.diseasePropertiesFile` and the output path) can be found. In the above example, the folder is `/data/thehague`. This means that the file paths for people, locations, activities, etc. can all be found in the folder `/data/thehague`, where the first `/` is relative to the location of the jar file that is being executed. 
- `generic.diseasePropertiesModel` can take two values: `distance` or `area`. This chooses one of two available disease transmission models, where one is based on the number of persons in an area, and the other is based on average distance between an infectious and a susceptible person. 
- `generic.PersonDumpIntervalDays` does not dump all persons in the model (490,000 for the The Hague model) with all their properties. When the interval is set at a N days, all persons in the model dump their state to a file every N days. This csv file can grow very large, therefore the default value is 0.
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

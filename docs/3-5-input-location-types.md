# MEDLABS - Agent-Based Simulation for Disease Spread in Cities and Regions

## 3.5. Input files: Location types

Since there are often over 200,000 locations in a typical simulation (every home is a separate location), we have to keep the locations themselves small, and store the common factors in a so-called Location Type. The Location Types are also used in the activity schedules of persons (find the nearest Supermarket -- a Location Type). 

Below is an example of a LocationType csv-file:

```
name,animationClass,reproducible,infectSub,infectTotal,contagiousRateFactor,
    infectionProbabilityFactor,capConstrained,capPersonsPerM2,sizeFactor
Accommodation,HouseAnimation,TRUE,TRUE,FALSE,1,1,FALSE,0.25,1.0
Workplace,WorkplaceAnimation,TRUE,TRUE,FALSE,1,1,FALSE,0.5,1.0
Retail,RetailAnimation,FALSE,TRUE,FALSE,1,1,TRUE,0.25,1.0
Mall,MallAnimation,FALSE,TRUE,FALSE,1,1,TRUE,0.1,7.4
BarRestaurant,RestaurantAnimation,FALSE,TRUE,FALSE,1,1,TRUE,0.5,1.0
FoodBeverage,FoodBeverageAnimation,FALSE,TRUE,FALSE,1,1,TRUE,0.5,1.3
Supermarket,SupermarketAnimation,FALSE,TRUE,FALSE,1,1,TRUE,0.2,9.7
Kindergarten,KindergartenAnimation,TRUE,TRUE,FALSE,1,1,FALSE,0.5,1.0
PrimarySchool,SchoolAnimation,TRUE,TRUE,FALSE,1,1,FALSE,0.5,1.0
SecondarySchool,SchoolAnimation,TRUE,TRUE,FALSE,1,1,FALSE,0.5,1.0
College,CollegeAnimation,TRUE,TRUE,FALSE,1,1,FALSE,0.5,1.0
University,UniversityAnimation,TRUE,TRUE,FALSE,1,1,FALSE,0.5,1.0
Religion,ReligionAnimation,TRUE,TRUE,FALSE,1,1,TRUE,0.5,1.0
Police,PoliceAnimation,TRUE,TRUE,FALSE,1,1,FALSE,0.5,1.0
FireStation,FireStationAnimation,TRUE,TRUE,FALSE,1,1,FALSE,0.5,1.0
Pharmacy,PharmacyAnimation,FALSE,TRUE,FALSE,1,1,TRUE,0.5,3.6
Healthcare,MedServiceAnimation,FALSE,TRUE,FALSE,1,1,TRUE,0.25,1.0
Hospital,HospitalAnimation,FALSE,TRUE,FALSE,1,1,TRUE,0.25,1.0
Recreation,RecreationAnimation,FALSE,TRUE,FALSE,1,1,TRUE,0.2,1.9
Park,ParkAnimation,FALSE,FALSE,FALSE,1,1,TRUE,0.1,1.0
Satellite accommodation,SatelliteAccAnimation,FALSE,FALSE,FALSE,1,1,FALSE,0.25,1.0
Satellite workplace,SatelliteWorkAnimation,FALSE,FALSE,FALSE,1,1,FALSE,0.25,1.0
```

The above example location type configuration file was a bit shortened for readability; for the `animationClass`, typically the full path is given: `nl.tudelft.simulation.medlabs.location.animation.defaults.HouseAnimation` instead of just `HouseAnimation`.

The following columns are present in the file:

- `name`: The name of the location type. Is used as a reference in the activity schedule of person types.
- `animationClass`: The symbol that is used to draw this location type.
- `reproducible`:  Whether the visit to this location by a person should result in the same sublocation or not. For a school or work location: yes. For a mall: no.
- `infectSub`: Does the infection take place in the sub-location (office in a large workplace; classroom in a school; shop in a mall).
- `infectTotal`: Does the infection take place based on everyone in the building? Note that `infectSub` and `infectTotal` can both be true. 
- `contagiousRateFactor`: Factor for the rate of contagiousness in this location type, default 1.0. This number can be reduced by e.g. social distancing and ventilation. In that case the factor is less than 1. The factor should be in the interval (0, 1].
- `infectionProbabilityFactor`: A multiplication factor to increase/decrease the probability of getting infected in that particular location type. This factor is currently ignored. Set to 1 for clarity.
- `capConstrained`: Is the capacity constrained? If false, only warnings will be given when the location is too full; if true: persons will not be admitted when the location is full.
- `capPersonsPerM2`: The maximum number per m2 of floor space allowed.
- `sizeFactor`: A factor to indicate that the actual space is 'larger' than the number of m2 would suggest. Correction factor for open spaces such as parks and recreation, and locations with 'separations' such as supermarkets.


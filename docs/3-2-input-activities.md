# MEDLABS
# Agent-Based Simulation for Disease Spread in Cities and Regions

## 3.2. Input files: Activities file

The activities file specified the activity patterns of different types of persons in the city, with probabilities where persons go, how long activities take, etc. The activities files can be based on national monitoring studies of person behavior, or based on assumptions such as how babies or school kids spend their day. The files make an explicit distinction between days of the week.

It is possible to switch the activity schedule when a policy is active, such as not going to work under a lockdown.

For instance, a susceptible worker has the following activity schedule for a Monday:

```
personal care  StochasticDurationActivity    
               triangular  6.5 6   7     
               HomeLocator       
travel         TravelActivityDistanceBased   
               0.25  0.33  
               DistanceBasedTravelLocator  HomeLocator WorkLocator   
work           StochasticDurationActivity    
               uniform   3 4   
               WorkLocator       
lunch          StochasticDurationActivity    
               uniform   0.5 0.75    
               CurrentLocator        
work           StochasticDurationActivity    
               triangular  4 3 5.5   
               WorkLocator       
travel         TravelActivityDistanceBased       
               0.25  0.33    
               DistanceBasedTravelLocator  CurrentLocator  HomeLocator   
personal care  StochasticDurationActivity    
               uniform 0.54  0.08  0.25    
               CurrentLocator        
travel         TravelActivityDistanceBased
               0.25  0.33    
               DistanceBasedTravelLocator  CurrentLocator  NearestLocator    
               LocationType.Retail: 0.084; LocationType.Supermarket: 0.25;
               LocationType.FoodBeverage: 0.21; LocationType.Mall: 0.056;
               LocationType.Pharmacy: 0.03; LocationType.Accommodation: 0.37;
shopping       StochasticDurationActivity    
               triangular  0.54  0.25  0.75    
               CurrentLocator        
travel         TravelActivityDistanceBased     
               0.54  0.16  0.25    
               DistanceBasedTravelLocator  CurrentLocator  HomeLocator   
dinner         StochasticDurationActivity    
               triangular  0.75  0.5 1   
               HomeLocator       
travel         TravelActivityDistanceBased       
               0.25  0.33    
               DistanceBasedTravelLocator  CurrentLocator  
               RandomLocator 2500
               LocationType.BarRestaurant:0.2; LocationType.Park:0.2; 
               LocationType.Recreation:0.05; LocationType.Accommodation:0.55;
social act     StochasticDurationActivity    
               triangular  1 0.5 1.5   
               CurrentLocator        
travel         TravelActivityDistanceBased
               0.25  0.33    
               DistanceBasedTravelLocator  CurrentLocator  HomeLocator   
personal care  UntilFixedTimeActivity  24
               HomeLocator       
```

### 3.2.1. Locators

The above example already shows that there are so-called 'Locators' in use. These are pointers to the place where the person will be during the activity. We have the following important locators:

- *HomeLocator* points to the location where this person lives.
- *WorkLocator* points to the location where this person works.
- *SchoolLocator* points to the location where this person goes to school.
- *CurrentLocator* points to the location where this person currently is, after completing the previous activity.
- *DistanceBasedTravelLocator* points to a mode of transport (car, bike, walking, public transport), depending on distance.
- *RandomLocator* chooses the next location randomly, based on a list of location types with a probability (LocationType.BarRestaurant:0.2; LocationType.Park:0.2, etc.) and a maximum distance to that location in meters.
- *NearestLocator* chooses the nearest location, after choosing a location type from a list of location types with a probability. The distance to the nearest location is calculated from the current location. A person who goes shopping after work, will do so close to the work location.
- There are several more pre-defined locators, see the package `nl.tudelft.simulation.medlabs.activity.locator` for all types. 


### 3.2.2. Activities

There are different types of activities:

- *FixedDurationActivity*. An activity that takes a fixed duration in hours, plus a locator that indicates where the activity takes place.
- *StochasticDurationActivity*. An activity that takes a stochastic duration (a distribution with parameters, in hours), plus a locator that indicates where the activity takes place.
- *UntilFixedTimeActivity* takes a time of day in hours, and fills the time until that hour with the given activity at the provided locator. This is used, e.g. for sleep till midnight. When the time has passed, the next activity starts right away.
- *TravelActivityDistanceBased* is a travel activity with travel means, based on distance. It takes a minimum and maximum number of hours, the locator for the transport, the start locator, and the end locator as arguments.
- There are several more, see the package `nl.tudelft.simulation.medlabs.activity` for all types. 


### 3.2.3. Excel activity file

The entire file for all person types, disease stages, and days of the week is offered as the activity file. The columns in Excel indicate what parameters are expected in each cell. 

In the example configuration, the file `activities/activityschedules.xlsx` gives a good indication of an overall set of activities of person types in a city.

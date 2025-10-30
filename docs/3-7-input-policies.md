# MEDLABS - Agent-Based Simulation for Disease Spread in Cities and Regions

## 3.7. Input files: Policies

The policy file indicates what will happen to the opening and closing of certain location types from a certain simulation day onward. An example file is:

```
Time(d),LocationType,FractionOpen,FractionActivities,AlternativeLocation,ReportAsLocation
10.0,Workplace,0.0,0.0,Accommodation,WorkToHome
10.0,Retail,0.0,0.0,Accommodation,StayHome
10.0,Mall,0.0,0.0,Accommodation,StayHome
10.0,BarRestaurant,0.0,0.0,Accommodation,StayHome
10.0,FoodBeverage,0.0,0.0,Accommodation,StayHome
10.0,Kindergarten,0.0,0.0,Accommodation,KindergartenToHome
10.0,PrimarySchool,0.0,0.0,Accommodation,PrimarySchoolToHome
10.0,SecondarySchool,0.0,0.0,Accommodation,SecondarySchooToHome
10.0,College,0.0,0.0,Accommodation,CollegeToHome
10.0,University,0.0,0.0,Accommodation,UniversityToHome
10.0,Recreation,0.0,0.0,Accommodation,StayHome
10.0,Park,0.0,0.0,Accommodation,StayHome
10.0,Satellite workplace,0.0,0.0,Accommodation,SatelliteWorkToHome
40.0,Workplace,1.0,1.0,Workplace,Workplace
40.0,Retail,1.0,1.0,Retail,Retail
40.0,Mall,1.0,1.0,Mall,Mall
40.0,BarRestaurant,1.0,1.0,BarRestaurant,BarRestaurant
40.0,FoodBeverage,1.0,1.0,FoodBeverage,FoodBeverage
40.0,Kindergarten,1.0,1.0,Kindergarten,Kindergarten
40.0,PrimarySchool,1.0,1.0,PrimarySchool,PrimarySchool
40.0,SecondarySchool,1.0,1.0,SecondarySchool,SecondarySchool
40.0,College,1.0,1.0,College,College
40.0,University,1.0,1.0,University,University
40.0,Recreation,1.0,1.0,Recreation,Recreation
40.0,Park,1.0,1.0,Park,Park
40.0,Satellite workplace,1.0,1.0,Satellite workplace,Satellite workplace
```

This policy causes a total lockdown from day 10 till day 40. Workplaces, schools and universities, retail, bars and restaurants, and recreation and parks will all be closed between these days. At day 10, the fraction of activities reduces to 0.0 (the activity schedules will not include these locations anymore), and the fraction open as well (the locations will not be available for people to enter). At day 40, it is increased to 1.0 again. it is also possible to limit access to, e.g., 20% of the original capacity by setting `FractionActivities` to 0.2. 

The `AlternativeLocation` indicates where people will spend time instead. `ReportAsLocation` is used for the output, so you can see where people spend their time as a *replacement* of an original activity. Examples are working from home and studying from home.

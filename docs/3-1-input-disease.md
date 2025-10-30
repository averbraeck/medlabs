# MEDLABS - Agent-Based Simulation for Disease Spread in Cities and Regions

## 3.1. Input files: Disease properties file

The disease properties file contains all information on the disease spread. The disease properties file consists of the following parts: a transmission probability model, which can be distance-based or area-based; and a disease progression model that indicates how patients go from disease stage to disease stage, and how long that takes.

### 3.1.1. Area-based transmission probability model

The area-based transmission is turned on in the main properties file with:

```
# whether the model is area based or distance based
generic.diseasePropertiesModel = area
```

For the area-based transmission model, we try to estimate the probability that person $i$ gets infected when zero or more infectious persons are available in that same area (an area is a sub-location of a location such as a house, workplace, school, shop, supermarket, etc.).

$$
p_i^{\text{infected}} = 1 - e^{-\sum_{j=1}^{N_K} 
\frac{\beta \cdot p_B \cdot p_j(t_e) \cdot t_{i,j}}
{\sigma_T \cdot A_K}}
$$

Since $\beta$, $p_B$, $t_{i,j}$, $\sigma_T$, and $A_K$ are constant in one calculation, the formula simplifies to:

$$
p_i^{\text{infected}} = 1 - e^{-
\frac{\beta \cdot p_B \cdot t_{i,j}}
{\sigma_T \cdot A_K}
\sum_{j=1}^{N_K} p_j(t_e)}
$$

where:

- $T$ is the location type of the location  
- $K$ is the indicator of the (sub)location  
- $N_K$ is the number of infectious persons in (sub)location $K$ which is of location type $T$  
- $\sigma_T$ is a correction factor for ventilation and social distancing for location type $T$  
  (making the location seem larger or smaller than it actually is); $\sigma_T \in (0, 1]$  
- $\beta$ is a correction factor for mask wearing and other personal protection; $\beta \in [0, 1]$  
- $p_B$ is the base contagiousness of the variant (base transmission without ventilation, masks, etc.)  
- $p_j(t_e)$ is the infectiousness of person $j$ for the number of days since the exposure date $t_e$ of person $j$;  
  e.g., the infectiousness can be 0 for the first 3 days, then climb to 1 in 4 days, and then decrease to 0 in about a week.  
  A person would then be contagious between day 3 and 14, with a peak at day 7 after exposure.  
  $p_j(t_e) \in [0, 1]$  
- $t_{i,j}$ is the time that contagious person $j$ and susceptible person $i$ have spent together in location $K$ (in hours)  

---

**Example"**

Suppose that one infectious person and one susceptible person spend 1 hour together in a room of 10 m²,  
with the infectiousness of person $j$ at its peak.  
When there are no corrective measures, the formula simplifies to:

$$
p_i = 1 - e^{-\frac{p_B}{10}}
$$

Suppose the chance of getting infected is 0.05 (5%) in that case. Then:

$$
e^{-\frac{p_B}{10}} = 0.95 \quad \Rightarrow \quad 
p_B = -10 \ln(0.95) = 0.51
$$

When the room is 5 m², the probability becomes 9.6%.  
When the persons spend 2 hours together, the probability also becomes 9.6%.  
When the persons wear masks, reducing transmission by 50% ($\beta = 0.5$),  
the transmission probability becomes 2.5%.  
When the room is extremely large (e.g., outside, $A_K = 1000$ m²):

$$
p_i = 1 - e^{-0.001} \approx 10^{-3}
$$


### 3.1.2. Distance-based transmission probability model

The distance-based transmission is turned on in the main properties file with:

```
# whether the model is area based or distance based
generic.diseasePropertiesModel = distance
```

For the area-based transmission model, we try to estimate the probability that person $i$ gets infected when zero or more infectious persons are available in that same area (an area is a sub-location of a location such as a house, workplace, school, shop, supermarket, etc.). 

Calculate the disease spread for all persons present in this (sub)location during the 'duration' in hours. The method
could return quickly when the delta-time is very short (e.g, less than a minute but be aware that spread in public
transport and shops might suffer since many people arrive and depart with short intervals).<br>

The formula to compute whether infectious persons $j = 1 \dots N_K$ infect another person $i$ in location $K$ is:

$$
p_i = 1 - \exp\left(- \sum_{j=1}^{M_k}
  (1-\mu)^2 \cdot P_j(d) \cdot t_{i,j}
  \cdot \sigma\big(\max(\Delta(A_k, N_k), \psi)\big)
  \cdot \alpha
  \right)
$$

where:

- $k$ is the (sub)location index  
- $i$ is the index of a susceptible person in (sub)location $k$  
- $j$ is the index of an infectious person in (sub)location $k$  
- $M_k$ is the number of infectious persons in (sub)location $k$  
- $N_k$ is the number of persons in (sub)location $K$ which is of location type $T$  
- $P_j(d)$ is the infectiousness of person $j$ for the number of days $d$ since the exposure date of person $j$;  
  for example, infectiousness can be 0 for the first 3 days, then climb to 7 in 4 days, and then decrease to 0  
  in about a week  
- $\mu$ is the masking factor, between 0 (no masks) and 1 (fully protected)  
- $t_{i,j}$ is the time that contagious person $j$ and susceptible person $i$ have spent together in location $K$ (in hours)  
- $\sigma$ is the function that translates average distance to transmission probability  
- $\Delta$ is the function that transforms area $A_k$ to average distance  
- $A_k$ is the area of (sub)location $k$  
- $\psi$ is the social distancing factor (minimum distance people keep)  
- $\alpha$ is a calibration factor


### 3.1.3. Disease progression model

The disease progression model is an extended variant of an SEIRD model (S = Susceptible, E = Exposed, I = Infectious, R = recovered, D = died), where the stage I is split into *asymptomatic* I(A) and *symptomatic* I(S). State I(S) can lead to a hospitalized state I(H) and possibly an intensive care stay I(I). 

- The transition S -> E is determined by the Transmission model
- Probability and Duration distribution E -> I(A)<br>
  we call this the incubation period (person is not ill and not contagious) 
- Probability and Duration distribution E -> I(S)<br>
  we call this the incubation period (person is not ill and not contagious)
- Duration distribution I(A) -> R<br>
  we assume I(A) always leads to R
- Probability and Duration distribution I(S) -> R<br>
  a certain percentage recovers without going to the hospital
- Probability and Duration distribution I(S) -> I(H)<br>
  a certain percentage of people gets hospitalized
- Probability and Duration distribution I(H) -> R<br>
  a certain percentage of hospitalized people recover
- Probability and Duration distribution I(H) -> I(I)<br>
  a certain percentage of hospitalized people go to the ICU
- Probability and Duration distribution I(H) -> D<br>
  a certain percentage of hospitalized people die
- Probability and Duration distribution I(I) -> R<br>
  a certain percentage of people in the ICU recover
- Probability and Duration distribution I(I) -> D<br>
  a certain percentage of people in the ICU die

For all state transitions, the *probability* and *duration* is given in the disease configuration file.

Many fractions can be given for the whole population, or with differences for subgroups:
- single number between 0 and 1, the same fraction or probability for the entire population
- age dependent parameter, e.g., age{0-19: 0.8, 20-55: 0.5, 56-100: 0.3}
- gender dependent parameter, e.g., gender{M:0.45, F:0.5}

Time is given as a draw from a distribution. Examples are:
- Triangular(7,12,14), where the parameters are (min, mode, max)
- TruncatedNormal(12.0, 2.3, 12.0, 14.0), where the parameters are (mu, sigma, min, max)
- Constant(3)
- Uniform(2,4)

All times are in days. 



### 3.1.4. Example area-based configuration file

In the repository, under `/src/main/resources`, there is a file called `alpha-area.properties`. In the file, all properties start with `covidT_area`. This is the name of the GROUP under which the parameters will be stored. This group is also shown as a tab in the interactive application environment.

All variables relate to the models described above. 

```
# COVID ALPHA VARIANT

# COVID TRANSMISSION MODEL PARAMETERS
# ===================================

# base contagiousness parameter
covidT_area.contagiousness = 1.0

# initial variable for personal protection factor, to be multiplied with base contagiousness
covidT_area.beta = 1.0

# (t_e_min, t_e_mode, t_e_max) forms a triangular distribution indicating the viral load distribution over time

# rough calculation constant to indicate first day of contagiousness of an exposed person (days)
covidT_area.t_e_min = 2.0

# rough calculation constant to indicate peak day of contagiousness of an exposed person (days)
covidT_area.t_e_mode = 3.4

# rough calculation constant to indicate last day of contagiousness of an exposed person (days)
covidT_area.t_e_max = 9.6

# threshold for the transmission calculation. Set to 1 minute (seconds)
covidT_area.calculation_threshold = 60


# COVID PROGRESSION MODEL PARAMETERS
# ==================================

# The transition S -> E is determined by the Transmission model
# Probability and Duration distribution E -> I(A)     we call this the incubation period (person is not ill and not contagious) 
# Probability and Duration distribution E -> I(S)     we call this the incubation period (person is not ill and not contagious)
# Duration distribution I(A) -> R                     we assume I(A) always leads to R
# Probability and Duration distribution I(S) -> R     a certain percentage recovers without going to the hospital
# Probability and Duration distribution I(S) -> I(H)  a certain percentage of people gets hospitalized
# Probability and Duration distribution I(H) -> R     a certain percentage of hospitalized people recover
# Probability and Duration distribution I(H) -> I(I)  a certain percentage of hospitalized people go to the ICU
# Probability and Duration distribution I(H) -> D     a certain percentage of hospitalized people die
# Probability and Duration distribution I(I) -> R     a certain percentage of people in the ICU recover
# Probability and Duration distribution I(I) -> D     a certain percentage of people in the ICU die

# fraction that is asymptomatic. This can be:
# - single number between 0 and 1
# - age dependent parameter, e.g., age{0-19: 0.8, 20-55: 0.5, 56-100: 0.3}
# - gender dependent parameter, e.g., gender{M:0.45, F:0.5}
# symptomatic has a probability (1 - FractionAsymptomatic)
covidP.FractionAsymptomatic = 0.46

# incubation period Exposed to Asymptomatic E->I(A) (days) 
# Specify as a distribution, e.g. Triangular(2,3,4) or Constant(3) or Uniform(2,4)
covidP.IncubationPeriodAsymptomatic = Triangular(2.5, 3.4, 3.8)

# incubation period Exposed to Symptomatic E->I(S) (days) 
# Specify as a distribution, e.g. Triangular(2,3,4) or Constant(3) or Uniform(2,4)
covidP.IncubationPeriodSymptomatic = Triangular(2.5, 3.4, 3.8)

# Asymptomatic recovery period I(A)->R (days). 
# Note that the probability is assumed to be 1 (all asymptomatic persons recover).
# Specify as a distribution, e.g. Triangular(7,12,14) or TruncatedNormal(12.0, 2.3, 12.0, 14.0)
covidP.PeriodAsymptomaticToRecovered = Triangular(12, 16, 20)

# fraction I(S)->I(H). This can be:
# - single number between 0 and 1
# - age dependent parameter, e.g., age{0-19: 0.8, 20-55: 0.5, 56-100: 0.3}
# - gender dependent parameter, e.g., gender{M:0.45, F:0.5}
covidP.FractionSymptomaticToHospitalized = age{0-19: 0.02153, 20-29: 0.01648, 30-39: 0.05044, 40-49: 0.11142, 50-59: 0.20593, 60-69: 0.44038, 70-79: 0.60867, 80-89: 0.32301, 90-100: 0.12687}

# The fraction to recover via I(S)->R is (1 - FractionSymptomaticToHospitalized)

# period I(S)->I(H) in days.
# Specify as a distribution, e.g. Triangular(7,12,14) or TruncatedNormal(12.0, 2.3, 12.0, 14.0)
covidP.PeriodSymptomaticToHospitalized = Triangular(7, 9, 11)

# period I(S)->R in days.
# Specify as a distribution, e.g. Triangular(7,12,14) or TruncatedNormal(12.0, 2.3, 12.0, 14.0)
covidP.PeriodSymptomaticToRecovered = Triangular(12, 16, 20)

# fraction I(H)->I(I). This can be:
# - single number between 0 and 1
# - age dependent parameter, e.g., age{0-29: 0.0, 30-50: 0.1, 51-70: 0.2, 71-80: 0.4, 81-100: 0.3}
# - gender dependent parameter, e.g., gender{M:0.1, F:0.08}
covidP.FractionHospitalizedToICU = age{0-19: 0.00152, 20-29: 0.00245, 30-39: 0.00921, 40-49: 0.02614, 50-59: 0.05829, 60-69: 0.14674, 70-79: 0.15508, 80-89: 0.01647, 90-100: 0}

# fraction I(H)->D. This can be:
# - single number between 0 and 1
# - age dependent parameter, e.g., age{0-29: 0.0, 30-50: 0.1, 51-70: 0.2, 71-80: 0.4, 81-100: 0.3}
# - gender dependent parameter, e.g., gender{M:0.1, F:0.08}
covidP.FractionHospitalizedToDead = 0.0

# The fraction I(H)->R is the "rest" fraction when neither I(H)->I(I) nor I(H)->D has been drawn

# Period I(H)->I(I) in days.
# Specify as a distribution, e.g. Triangular(7,12,14) or TruncatedNormal(12.0, 2.3, 12.0, 14.0)
covidP.PeriodHospitalizedToICU = Triangular(1,3,5)

# Period I(H)->D in days.
# Specify as a distribution, e.g. Triangular(7,12,14) or TruncatedNormal(12.0, 2.3, 12.0, 14.0)
covidP.PeriodHospitalizedToDead = Triangular(1,3,5)

# Period I(H)->R in days.
# Specify as a distribution, e.g. Triangular(7,12,14) or TruncatedNormal(12.0, 2.3, 12.0, 14.0)
covidP.PeriodHospitalizedToRecovered = Triangular(11,13,15)

# fraction I(I)->D. This can be:
# - single number between 0 and 1
# - age dependent parameter, e.g., age{0-29: 0.0, 30-50: 0.1, 51-70: 0.2, 71-80: 0.4, 81-100: 0.3}
# - gender dependent parameter, e.g., gender{M:0.1, F:0.08}
covidP.FractionICUToDead = age{0-49: 0, 50-59: 0.01452, 60-69: 0.08393, 70-79: 0.39731, 80-89: 0.63002, 90-100: 0.67882}

# The fraction I(I)->R is (1 - FractionICUToDead)

# Period I(I)->D in days.
# Specify as a distribution, e.g. Triangular(7,12,14) or TruncatedNormal(12.0, 2.3, 12.0, 14.0)
covidP.PeriodICUToDead = Triangular(2,4,6)

# Period I(I)->R in days.
# Specify as a distribution, e.g. Triangular(7,12,14) or TruncatedNormal(12.0, 2.3, 12.0, 14.0)
covidP.PeriodICUToRecovered = Triangular(28,30,32)
```


### 3.1.5. Example distance-based configuration file

In the example configuration, the file `alpha-distance.properties` is used:

```
# COVID ALPHA VARIANT

# COVID TRANSMISSION MODEL PARAMETERS
# ===================================

# VIRAL LOAD MODEL
# ----------------

# Latent period L (days)
covidT_dist.L = 2.0

# Incubation period I (days). includes L, so I > L
covidT_dist.I = 3.4

# Clinical disease period C (days). C starts after L
covidT_dist.C = 6.2

# Peak viral load v_max (> 0)
covidT_dist.v_max = 7.23

# TRANSMISSION PROBABILITY
# ------------------------

# Reference viral load v_0 (> 0)
covidT_dist.v_0 = 4.0

# Transmission rate r (> 0)
covidT_dist.r = 2.294

# INFECTION PROBABILITY
# ---------------------

# Social distancing factor psi (> 0)
covidT_dist.psi = 1.0

# Calibration factor alpha (> 0)
covidT_dist.alpha = 0.05

# Mask effectiveness mu from interval [0, 1]
covidT_dist.mu = 0.0

# GENERIC PARAMETER
# -----------------

# threshold for the transmission calculation. Set to 1 minute (seconds)
covidT_dist.calculation_threshold = 60


# COVID PROGRESSION MODEL PARAMETERS
# ==================================

# The transition S -> E is determined by the Transmission model
# Probability and Duration distribution E -> I(A)     we call this the incubation period (person is not ill and not contagious) 
# Probability and Duration distribution E -> I(S)     we call this the incubation period (person is not ill and not contagious)
# Duration distribution I(A) -> R                     we assume I(A) always leads to R
# Probability and Duration distribution I(S) -> R     a certain percentage recovers without going to the hospital
# Probability and Duration distribution I(S) -> I(H)  a certain percentage of people gets hospitalized
# Probability and Duration distribution I(H) -> R     a certain percentage of hospitalized people recover
# Probability and Duration distribution I(H) -> I(I)  a certain percentage of hospitalized people go to the ICU
# Probability and Duration distribution I(H) -> D     a certain percentage of hospitalized people die
# Probability and Duration distribution I(I) -> R     a certain percentage of people in the ICU recover
# Probability and Duration distribution I(I) -> D     a certain percentage of people in the ICU die

# fraction that is asymptomatic. This can be:
# - single number between 0 and 1
# - age dependent parameter, e.g., age{0-19: 0.8, 20-55: 0.5, 56-100: 0.3}
# - gender dependent parameter, e.g., gender{M:0.45, F:0.5}
# symptomatic has a probability (1 - FractionAsymptomatic)
covidP.FractionAsymptomatic = 0.46

# incubation period Exposed to Asymptomatic E->I(A) (days) 
# Specify as a distribution, e.g. Triangular(2,3,4) or Constant(3) or Uniform(2,4)
covidP.IncubationPeriodAsymptomatic = Triangular(2.5, 3.4, 3.8)

# incubation period Exposed to Symptomatic E->I(S) (days) 
# Specify as a distribution, e.g. Triangular(2,3,4) or Constant(3) or Uniform(2,4)
covidP.IncubationPeriodSymptomatic = Triangular(2.5, 3.4, 3.8)

# Asymptomatic recovery period I(A)->R (days). 
# Note that the probability is assumed to be 1 (all asymptomatic persons recover).
# Specify as a distribution, e.g. Triangular(7,12,14) or TruncatedNormal(12.0, 2.3, 12.0, 14.0)
covidP.PeriodAsymptomaticToRecovered = Triangular(12, 16, 20)

# fraction I(S)->I(H). This can be:
# - single number between 0 and 1
# - age dependent parameter, e.g., age{0-19: 0.8, 20-55: 0.5, 56-100: 0.3}
# - gender dependent parameter, e.g., gender{M:0.45, F:0.5}
covidP.FractionSymptomaticToHospitalized = age{0-19: 0.02153, 20-29: 0.01648, 30-39: 0.05044, 40-49: 0.11142, 50-59: 0.20593, 60-69: 0.44038, 70-79: 0.60867, 80-89: 0.32301, 90-100: 0.12687}

# The fraction to recover via I(S)->R is (1 - FractionSymptomaticToHospitalized)

# period I(S)->I(H) in days.
# Specify as a distribution, e.g. Triangular(7,12,14) or TruncatedNormal(12.0, 2.3, 12.0, 14.0)
covidP.PeriodSymptomaticToHospitalized = Triangular(7, 9, 11)

# period I(S)->R in days.
# Specify as a distribution, e.g. Triangular(7,12,14) or TruncatedNormal(12.0, 2.3, 12.0, 14.0)
covidP.PeriodSymptomaticToRecovered = Triangular(12, 16, 20)

# fraction I(H)->I(I). This can be:
# - single number between 0 and 1
# - age dependent parameter, e.g., age{0-29: 0.0, 30-50: 0.1, 51-70: 0.2, 71-80: 0.4, 81-100: 0.3}
# - gender dependent parameter, e.g., gender{M:0.1, F:0.08}
covidP.FractionHospitalizedToICU = age{0-19: 0.00152, 20-29: 0.00245, 30-39: 0.00921, 40-49: 0.02614, 50-59: 0.05829, 60-69: 0.14674, 70-79: 0.15508, 80-89: 0.01647, 90-100: 0}

# fraction I(H)->D. This can be:
# - single number between 0 and 1
# - age dependent parameter, e.g., age{0-29: 0.0, 30-50: 0.1, 51-70: 0.2, 71-80: 0.4, 81-100: 0.3}
# - gender dependent parameter, e.g., gender{M:0.1, F:0.08}
covidP.FractionHospitalizedToDead = 0.0

# The fraction I(H)->R is the "rest" fraction when neither I(H)->I(I) nor I(H)->D has been drawn

# Period I(H)->I(I) in days.
# Specify as a distribution, e.g. Triangular(7,12,14) or TruncatedNormal(12.0, 2.3, 12.0, 14.0)
covidP.PeriodHospitalizedToICU = Triangular(1,3,5)

# Period I(H)->D in days.
# Specify as a distribution, e.g. Triangular(7,12,14) or TruncatedNormal(12.0, 2.3, 12.0, 14.0)
covidP.PeriodHospitalizedToDead = Triangular(1,3,5)

# Period I(H)->R in days.
# Specify as a distribution, e.g. Triangular(7,12,14) or TruncatedNormal(12.0, 2.3, 12.0, 14.0)
covidP.PeriodHospitalizedToRecovered = Triangular(11,13,15)

# fraction I(I)->D. This can be:
# - single number between 0 and 1
# - age dependent parameter, e.g., age{0-29: 0.0, 30-50: 0.1, 51-70: 0.2, 71-80: 0.4, 81-100: 0.3}
# - gender dependent parameter, e.g., gender{M:0.1, F:0.08}
covidP.FractionICUToDead = age{0-49: 0, 50-59: 0.01452, 60-69: 0.08393, 70-79: 0.39731, 80-89: 0.63002, 90-100: 0.67882}

# The fraction I(I)->R is (1 - FractionICUToDead)

# Period I(I)->D in days.
# Specify as a distribution, e.g. Triangular(7,12,14) or TruncatedNormal(12.0, 2.3, 12.0, 14.0)
covidP.PeriodICUToDead = Triangular(2,4,6)

# Period I(I)->R in days.
# Specify as a distribution, e.g. Triangular(7,12,14) or TruncatedNormal(12.0, 2.3, 12.0, 14.0)
covidP.PeriodICUToRecovered = Triangular(28,30,32)
```
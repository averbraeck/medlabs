# MEDLABS

## Agent-Based Simulation for Disease Spread in Cities and Regions 

MedLabs is a Java library for modeling disease spread on the level of individual humans in a city or region. The models contain agent-based representatives of persons, who carry out their normal activities during the day, where they can come into contact with the disease, either through contact with other persons, proximity to other persons, contact with disease vectors, or contact with objects or food that carry the disease.

MedLabs has been set-up as an open source simulation library for disease spread, and it is available under a BSD-3 license, which means that it is allowed to use the software for any purpose, including commercial use.

The original MedLabs libraries have been developed in the PhD thesis work of Dr. Mingxing Zhang, who defended his PhD thesis called "Large-Scale Agent-Based Social Simulation - A study on epidemic prediction and control" in 2016. The thesis is available from the [TU Delft Repository](https://doi.org/10.4233/uuid:8d0f67a3-d8e6-43ee-acc5-1633c617e023). The main ideas have been published in the Journal of Artificial Societies and Social Simulation (JASSS): Zhang et al (2016) Modeling Spatial Contacts for Epidemic Prediction in a Large-Scale Artificial City. [doi: 10.18564/jasss.3148](https://doi.org/10.18564/jasss.3148). 

This documentation explains how to run a medlabs model, what configuration files and input files are used, and what output is created during the run. A small toy example is available in the `demo` package. An extensive run for a real city building on the medlabs project can be found in the [medlabs-heros](https://github.com/averbraeck/medlabs-heros) project.

**Table of Contents:**

1. [Installing Java and running the code](docs/1-install.md)
2. [Configuration files](docs/2-configure.md)
3. [Input files](docs/3-input.md)<br>
   3.1. [Disease properties file](docs/3-1-input-disease.md).<br>
   3.2. [Activity schedules](docs/3-2-input-activities.md).<br>
   3.3. [Probabilistic infection rates](docs/3-3-input-infection-rates.md).<br>
   3.4. [People](docs/3-4-input-people.md).<br>
   3.5. [Location types](docs/3-5-input-location-types.md).<br>
   3.6. [Locations](docs/3-6-input-locations.md).<br>
   3.7. [Policies](docs/3-7-input-policies.md).<br>
4. [Output files](docs/4-output.md)
5. [Exploring and adapting the code](docs/5-code.md)
6. [Literature](docs/6-literature.md)


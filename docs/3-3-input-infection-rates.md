# MEDLABS - Agent-Based Simulation for Disease Spread in Cities and Regions

## 3.3. Input files: Probabilistic infection rates

For persons who do not live in the city, but come either from abroad or from satellite cities or other cities, it is impossible to estimate the infection rate, since their behavior outside the city is not modeled in detail. Therefore, the R0 factor is provided in a scenario file for these persons coming from poutside the city of interest.

Here is an example of a (simple) file with columns:

- `location_id`: id of the location in the location file.
- `infection_rate_factor`: the factor as compared to the infection rate in the city of interest. The `infection_rate_factor` and the current R0 in the city will be multiplied to calculate the R0 for the persons from that region. In a sense, the infection rates 'follow' what happens in the city of interest. Use -1 if not applicable.
- `infection_rate`: An autonomous infection rate for that region, for the entire run of the model. Use -1 if not applicable. Do not use BOTH `infection_rate_factor` and `infection_rate`.
- `location_category`: The name of the location category.
- `city`: the name of the city or -1 if not applicable.
- `country`: the name of the country.

An example file looks as follows:

```
location_id,infection_rate_factor,infection_rate,location_category,city,country
143158,0.8739049641624635,-1.0,Satellite workplace,Zoetermeer,The Netherlands
143159,0.8739049641624635,-1.0,Satellite accommodation,Zoetermeer,The Netherlands
143160,0.981152110432705,-1.0,Satellite workplace,Westland,The Netherlands
143161,0.981152110432705,-1.0,Satellite accommodation,Westland,The Netherlands
143162,0.9723918237324132,-1.0,Satellite workplace,Delft,The Netherlands
143163,0.9723918237324132,-1.0,Satellite accommodation,Delft,The Netherlands
143164,0.7894876559596495,-1.0,Satellite workplace,Leidschendam-Voorburg,The Netherlands
143165,0.7894876559596495,-1.0,Satellite accommodation,Leidschendam-Voorburg,The Netherlands
143166,0.9177063976639236,-1.0,Satellite workplace,Pijnacker-Nootdorp,The Netherlands
143167,0.9177063976639236,-1.0,Satellite accommodation,Pijnacker-Nootdorp,The Netherlands
143168,0.9123971329970798,-1.0,Satellite workplace,Rijswijk,The Netherlands
143169,0.9123971329970798,-1.0,Satellite accommodation,Rijswijk,The Netherlands
143170,0.6641890098221396,-1.0,Satellite workplace,Wassenaar,The Netherlands
143171,0.6641890098221396,-1.0,Satellite accommodation,Wassenaar,The Netherlands
143172,0.8959384125298646,-1.0,Satellite workplace,Midden-Delfland,The Netherlands
143173,0.8959384125298646,-1.0,Satellite accommodation,Midden-Delfland,The Netherlands
143174,-1.0,0.0009620898197102543,Satellite workplace,-1,Belgium
143175,-1.0,0.0009620898197102543,Satellite accommodation,-1,Belgium
143176,-1.0,0.00022007389738211747,Satellite workplace,-1,Germany
143177,-1.0,0.00022007389738211747,Satellite accommodation,-1,Germany
```

In the example, the file is stored in the data folder under `epidemiology/infection_rates.csv`.

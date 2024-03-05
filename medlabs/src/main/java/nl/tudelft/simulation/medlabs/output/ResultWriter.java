package nl.tudelft.simulation.medlabs.output;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

import org.djutils.event.Event;
import org.djutils.event.EventListener;

import gnu.trove.map.TIntDoubleMap;
import nl.tudelft.simulation.medlabs.activity.ActivityMonitor;
import nl.tudelft.simulation.medlabs.common.MedlabsRuntimeException;
import nl.tudelft.simulation.medlabs.disease.DiseasePhase;
import nl.tudelft.simulation.medlabs.disease.DiseaseProgression;
import nl.tudelft.simulation.medlabs.location.Location;
import nl.tudelft.simulation.medlabs.location.LocationType;
import nl.tudelft.simulation.medlabs.model.MedlabsModelInterface;
import nl.tudelft.simulation.medlabs.person.Person;
import nl.tudelft.simulation.medlabs.person.PersonMonitor;
import nl.tudelft.simulation.medlabs.person.PersonType;
import nl.tudelft.simulation.medlabs.person.Student;
import nl.tudelft.simulation.medlabs.person.Worker;

/**
 * ResultWriter writes simulation results to output files periodically.
 * <p>
 * Copyright (c) 2014-2024 Delft University of Technology, Jaffalaan 5, 2628 BX
 * Delft, the Netherlands. All rights reserved. The MEDLABS project (Modeling
 * Epidemic Disease with Large-scale Agent-Based Simulation) is aimed at
 * providing policy analysis tools to predict and help contain the spread of
 * epidemics. It makes use of the DSOL simulation engine and the agent-based
 * modeling formalism. See for project information
 * <a href="http://www.simulation.tudelft.nl/"> www.simulation.tudelft.nl</a>.
 * The original MEDLABS Java library was developed as part of the PhD research
 * of Mingxin Zhang at TU Delft and is described in the PhD thesis "Large-Scale
 * Agent-Based Social Simulation" (2016). This software is licensed under the
 * BSD license. See license.txt in the main project.
 * </p>
 * 
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class ResultWriter implements EventListener {
	/** */
	private static final long serialVersionUID = 20201005L;

	/** the model. */
	private final MedlabsModelInterface model;

	/** The locationtype file. */
	private PrintWriter locationTypeWriter;

	/** The diseasephase file. */
	private PrintWriter diseasePhaseWriter;

	/** The infections per location file. */
	private PrintWriter infectionLocationWriter;

	/** The infections per age bracket file. */
	private PrintWriter infectionAgeWriter;

	/** The deaths per age bracket file. */
	private PrintWriter deathsAgeWriter;

	/** The person dump file. */
	private PrintWriter personDumpWriter;

	/** The file with detailed information about an infection. */
	private PrintWriter infectedPersonWriter;

	/** The file with detailed information about a dead person. */
	private PrintWriter deadPersonWriter;

	/** The file with the number of infections per person type per day. */
	private PrintWriter dayInfPersonWriter;

	/** The file with the total number of infections per person type. */
	private PrintWriter totInfPersonWriter;

	/**
	 * The file with the number of infections from a person type to a person type
	 * per day.
	 */
	private PrintWriter dayInfPersonToPersonWriter;

	/**
	 * The file with the total number of infections from a person type to a person
	 * type.
	 */
	private PrintWriter totInfPersonToPersonWriter;

	/**
	 * The file with the number of infections per location type from a person type
	 * to a person type per day.
	 */
	private PrintWriter dayInfLocPersonToPersonWriter;

	/**
	 * The file with the total number of infections per location type from a person
	 * type to a person type.
	 */
	private PrintWriter totInfLocPersonToPersonWriter;

	/** The file with the infections by rate. */
	private PrintWriter infByRateWriter;

	/** The file with the infections by rate factor. */
	private PrintWriter infByRateFactorWriter;

	/** The file with the activity hours per day. */
	private PrintWriter dayActivityWriter;

	/** The file with the total activity hours. */
	private PrintWriter totActivityWriter;

	/**
	 * Create a writer of results to file.
	 * 
	 * @param model      the model
	 * @param outputPath the output path to which the filenames will be appended
	 */
	public ResultWriter(final MedlabsModelInterface model, final String outputPath) {
		this.model = model;
		makeOutputDirectory(outputPath);

		try {
			this.locationTypeWriter = new PrintWriter(outputPath + "/locationTypeNrs.csv");
			writeLocationTypeHeader();
			writeLocationTypeLine();

			DiseaseProgression disease = model.getDiseaseProgression();
			this.diseasePhaseWriter = new PrintWriter(outputPath + "/diseasePhaseNrs_" + disease.getName() + ".csv");
			writeDiseasePhaseHeader(disease);
			writeDiseasePhaseLine(disease);

			this.infectionLocationWriter = new PrintWriter(outputPath + "/infectionsPerLocation.csv");
			writeInfectionLocationHeader();
			model.getPersonMonitor().addListener(this, PersonMonitor.INFECT_ALL_LOCATIONTYPES_PER_HOUR_EVENT);

			this.infectionAgeWriter = new PrintWriter(outputPath + "/infectionsPerAge.csv");
			writeInfectionAgeHeader();
			model.getPersonMonitor().addListener(this, PersonMonitor.INFECT_AGE_PER_HOUR_EVENT);

			this.deathsAgeWriter = new PrintWriter(outputPath + "/deathsPerAge.csv");
			writeDeathsAgeHeader();
			model.getPersonMonitor().addListener(this, PersonMonitor.DEATHS_AGE_PER_DAY_EVENT);

			int personDumpInterval = model.getParameterValueInt("generic.PersonDumpIntervalDays");
			if (personDumpInterval > 0) {
				this.personDumpWriter = new PrintWriter(outputPath + "/personDump.csv");
				writePersonDumpHeader();
				writePersonDump(personDumpInterval);
			}

			this.infectedPersonWriter = new PrintWriter(outputPath + "/infectedPersons.csv");
			writeInfectedPersonHeader();
			model.getPersonMonitor().addListener(this, PersonMonitor.INFECTED_PERSON_EVENT);

			this.deadPersonWriter = new PrintWriter(outputPath + "/deadPersons.csv");
			writeDeadPersonHeader();
			model.getPersonMonitor().addListener(this, PersonMonitor.DEAD_PERSON_EVENT);

			this.dayInfPersonWriter = new PrintWriter(outputPath + "/dayInfPersonType.csv");
			writeDayInfPersonTypeHeader();
			model.getPersonMonitor().addListener(this, PersonMonitor.DAY_INFECTIONS_PERSON_TYPE);

			this.totInfPersonWriter = new PrintWriter(outputPath + "/totInfPersonType.csv");
			writeTotInfPersonTypeHeader();
			model.getPersonMonitor().addListener(this, PersonMonitor.TOT_INFECTIONS_PERSON_TYPE);

			this.dayInfPersonToPersonWriter = new PrintWriter(outputPath + "/dayInfPersonTypeToPersonType.csv");
			writeDayInfPersonToPersonTypeHeader();
			model.getPersonMonitor().addListener(this, PersonMonitor.DAY_INFECTIONS_PERSON_TO_PERSON_TYPE);

			this.totInfPersonToPersonWriter = new PrintWriter(outputPath + "/totInfPersonTypeToPersonType.csv");
			writeTotInfPersonToPersonTypeHeader();
			model.getPersonMonitor().addListener(this, PersonMonitor.TOT_INFECTIONS_PERSON_TO_PERSON_TYPE);

			this.dayInfLocPersonToPersonWriter = new PrintWriter(outputPath + "/dayInfLocPersonTypeToPersonType.csv");
			writeDayInfLocPersonToPersonTypeHeader();
			model.getPersonMonitor().addListener(this, PersonMonitor.DAY_INFECTIONS_LOC_PERSON_TO_PERSON_TYPE);

			this.totInfLocPersonToPersonWriter = new PrintWriter(outputPath + "/totInfLocPersonTypeToPersonType.csv");
			writeTotInfLocPersonToPersonTypeHeader();
			model.getPersonMonitor().addListener(this, PersonMonitor.TOT_INFECTIONS_LOC_PERSON_TO_PERSON_TYPE);

			this.infByRateWriter = new PrintWriter(outputPath + "/infectionsByRate.csv");
			writeInfByRateHeader();
			model.getPersonMonitor().addListener(this, PersonMonitor.INFECTION_BY_RATE);

			this.infByRateFactorWriter = new PrintWriter(outputPath + "/infectionsByRateFactor.csv");
			writeInfByRateFactorHeader();
			model.getPersonMonitor().addListener(this, PersonMonitor.INFECTION_BY_RATE_FACTOR);

			this.dayActivityWriter = new PrintWriter(outputPath + "/dayActivityTimes.csv");
			writeDayActivityHeader();
			model.getActivityMonitor().addListener(this, ActivityMonitor.ACTIVITY_DAY_STATISTICS_EVENT);

			this.totActivityWriter = new PrintWriter(outputPath + "/totActivityTimes.csv");
			writeTotActivityHeader();
			model.getActivityMonitor().addListener(this, ActivityMonitor.ACTIVITY_TOT_STATISTICS_EVENT);
		} catch (IOException ioe) {
			throw new MedlabsRuntimeException(ioe);
		}
	}

	/**
	 * make the output path + directory.
	 * 
	 * @param directory string; the full path to the output directory to create
	 */
	private void makeOutputDirectory(final String directory) {
		// try to create directory
		File f = new File(directory);
		if (!f.exists()) {
			try {
				if (!f.mkdirs()) {
					throw new Exception("Could not create directory for output: " + directory);
				}
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
		}
	}

	private void writeLocationTypeHeader() {
		this.locationTypeWriter.print("\"Time(h)\"");
		for (LocationType locationType : this.model.getLocationTypeIndexMap().values()) {
			this.locationTypeWriter.print(",\"" + locationType.getName() + "\"");
		}
		this.locationTypeWriter.write("\n");
		this.locationTypeWriter.flush();
	}

	private void writeLocationTypeLine() {
		this.locationTypeWriter.print(this.model.getSimulator().getSimulatorTime());
		for (LocationType locationType : this.model.getLocationTypeIndexMap().values()) {
			int nr = 0;
			for (Location location : locationType.getLocationMap().valueCollection()) {
				nr += location.getAllPersonIds().size();
			}
			this.locationTypeWriter.print("," + nr);
		}
		this.locationTypeWriter.write("\n");
		this.locationTypeWriter.flush();
		this.model.getSimulator().scheduleEventRel(0.5, this, this, "writeLocationTypeLine", null);
	}

	private void writeDiseasePhaseHeader(final DiseaseProgression disease) {
		this.diseasePhaseWriter.print("\"Time(h)\"");
		for (DiseasePhase diseasePhase : disease.getDiseasePhases()) {
			this.diseasePhaseWriter.print(",\"" + diseasePhase.getName() + "\"");
		}
		this.diseasePhaseWriter.write("\n");
		this.diseasePhaseWriter.flush();
	}

	private void writeDiseasePhaseLine(final DiseaseProgression disease) {
		this.diseasePhaseWriter.print(this.model.getSimulator().getSimulatorTime());
		for (DiseasePhase diseasePhase : disease.getDiseasePhases()) {
			this.diseasePhaseWriter.print("," + diseasePhase.getNumberOfPersons());
		}
		this.diseasePhaseWriter.write("\n");
		this.diseasePhaseWriter.flush();
		this.model.getSimulator().scheduleEventRel(0.5, this, this, "writeDiseasePhaseLine", new Object[] { disease });
	}

	private void writeInfectionLocationHeader() {
		this.infectionLocationWriter.print("\"Time(h)\"");
		for (LocationType locationType : this.model.getLocationTypeIndexMap().values()) {
			this.infectionLocationWriter.print(",\"" + locationType.getName() + "\"");
		}
		this.infectionLocationWriter.write("\n");
		this.infectionLocationWriter.flush();
	}

	private void writeInfectionLocationLine(final Map<LocationType, Integer> infections) {
		this.infectionLocationWriter.print(this.model.getSimulator().getSimulatorTime());
		for (LocationType locationType : this.model.getLocationTypeIndexMap().values()) {
			this.infectionLocationWriter.print("," + infections.get(locationType));
		}
		this.infectionLocationWriter.write("\n");
		this.infectionLocationWriter.flush();
	}

	private void writeInfectionAgeHeader() {
		this.infectionAgeWriter.print("\"Time(h)\"");
		for (int ageBracket = 0; ageBracket < 11; ageBracket++) {
			String bracket = (10 * ageBracket) + "-" + (10 * (ageBracket + 1) - 1);
			this.infectionAgeWriter.print(",\"" + bracket + "\"");
		}
		this.infectionAgeWriter.write("\n");
		this.infectionAgeWriter.flush();
	}

	private void writeInfectionAgeLine(final int[] infectionsPerAgeBracket) {
		this.infectionAgeWriter.print(this.model.getSimulator().getSimulatorTime());
		for (int i = 0; i < infectionsPerAgeBracket.length; i++) {
			this.infectionAgeWriter.print("," + infectionsPerAgeBracket[i]);
		}
		this.infectionAgeWriter.write("\n");
		this.infectionAgeWriter.flush();
	}

	private void writeDeathsAgeHeader() {
		this.deathsAgeWriter.print("\"Time(h)\"");
		for (int ageBracket = 0; ageBracket < 11; ageBracket++) {
			String bracket = (10 * ageBracket) + "-" + (10 * (ageBracket + 1) - 1);
			this.deathsAgeWriter.print(",\"" + bracket + "\"");
		}
		this.deathsAgeWriter.write("\n");
		this.deathsAgeWriter.flush();
	}

	private void writeDeathsAgeLine(final int[] deathsPerAgeBracket) {
		this.deathsAgeWriter.print(this.model.getSimulator().getSimulatorTime());
		for (int i = 0; i < deathsPerAgeBracket.length; i++) {
			this.deathsAgeWriter.print("," + deathsPerAgeBracket[i]);
		}
		this.deathsAgeWriter.write("\n");
		this.deathsAgeWriter.flush();
	}

	private void writePersonDumpHeader() {
		// this.personDumpWriter.println("\"Time(h)\",\"personId\",\"personType\",\"Age\",\"Gender\",\"homeId\",\"homeSubId\","
		// + "\"homeLat\",\"homeLon\",\"diseasePhase\",\"workId\",\"schoolId\"");

		this.personDumpWriter
				.println("\"Time(h)\",\"personId\",\"personType\",\"Age\",\"Gender\",\"homeId\",\"homeSubId\","
						+ "\"currentActivity\"," + "\"currentLat\",\"currentLon\","
						+ "\"homeLat\",\"homeLon\",\"diseasePhase\",\"workId\",\"schoolId\"");

		this.personDumpWriter.flush();
	}

	private void writePersonDump(final int personDumpInterval) {
		double time = this.model.getSimulator().getSimulatorTime();
		for (Person person : this.model.getPersonMap().valueCollection()) {
			this.personDumpWriter.print(time + "," + person.getId());
			this.personDumpWriter.print(",\"" + person.getClass().getSimpleName() + "\"");
			this.personDumpWriter.print("," + person.getAge());
			this.personDumpWriter.print("," + (person.getGenderFemale() ? "\"F\"" : "\"M\""));
			this.personDumpWriter.print("," + person.getHomeLocation().getId());
			this.personDumpWriter.print("," + person.getHomeSubLocationIndex());

			this.personDumpWriter.print("," + person.getCurrentActivity());

			Location currentLocation = this.model.getLocationMap().get(person.getCurrentLocation().getId());
			this.personDumpWriter.print("," + currentLocation.getLatitude());
			this.personDumpWriter.print("," + currentLocation.getLongitude());

			Location homeLocation = this.model.getLocationMap().get(person.getHomeLocation().getId());
			this.personDumpWriter.print("," + homeLocation.getLatitude());
			this.personDumpWriter.print("," + homeLocation.getLongitude());
			this.personDumpWriter.print(",\"" + person.getDiseasePhase().getName() + "\"");
			this.personDumpWriter
					.print("," + (person instanceof Worker ? ((Worker) person).getWorkLocation().getId() : -1));
			this.personDumpWriter
					.print("," + (person instanceof Student ? ((Student) person).getSchoolLocation().getId() : -1));
			this.personDumpWriter.println();
		}
		this.personDumpWriter.flush();

		// this.model.getSimulator().scheduleEventRel(24.0 * personDumpInterval, this,
		// this, "writePersonDump",
		// new Object[] { personDumpInterval });
		this.model.getSimulator().scheduleEventRel(1.0 * personDumpInterval, this, this, "writePersonDump",
				new Object[] { personDumpInterval });
	}

	private void writeInfectedPersonHeader() {
		this.infectedPersonWriter
				.println("\"Time(h)\",\"personId\",\"personType\",\"Age\",\"Gender\",\"homeId\",\"homeSubId\","
						+ "\"homeLat\",\"homeLon\",\"diseasePhase\",\"workId\",\"schoolId\",\"infectLocationType\","
						+ "\"infectLocationId\",\"infectLocationLat\",\"infectLocationLon\"");
		this.infectedPersonWriter.flush();
	}

	private void writeInfectedPersonLine(final Object[] content) {
		Person person = (Person) content[0];
		Location infectLocation = (Location) content[1];
		double time = this.model.getSimulator().getSimulatorTime();
		this.infectedPersonWriter.print(time + "," + person.getId());
		this.infectedPersonWriter.print(",\"" + person.getClass().getSimpleName() + "\"");
		this.infectedPersonWriter.print("," + person.getAge());
		this.infectedPersonWriter.print("," + (person.getGenderFemale() ? "\"F\"" : "\"M\""));
		this.infectedPersonWriter.print("," + person.getHomeLocation().getId());
		this.infectedPersonWriter.print("," + person.getHomeSubLocationIndex());
		Location homeLocation = this.model.getLocationMap().get(person.getHomeLocation().getId());
		this.infectedPersonWriter.print("," + homeLocation.getLatitude());
		this.infectedPersonWriter.print("," + homeLocation.getLongitude());
		this.infectedPersonWriter.print(",\"" + person.getDiseasePhase().getName() + "\"");
		this.infectedPersonWriter
				.print("," + (person instanceof Worker ? ((Worker) person).getWorkLocation().getId() : -1));
		this.infectedPersonWriter
				.print("," + (person instanceof Student ? ((Student) person).getSchoolLocation().getId() : -1));
		this.infectedPersonWriter.print(",\"" + infectLocation.getLocationType().getName() + "\"");
		this.infectedPersonWriter.print("," + infectLocation.getId());
		this.infectedPersonWriter.print("," + infectLocation.getLatitude());
		this.infectedPersonWriter.print("," + infectLocation.getLongitude());
		this.infectedPersonWriter.println();
		this.infectedPersonWriter.flush();
	}

	private void writeDeadPersonHeader() {
		this.deadPersonWriter
				.println("\"Time(h)\",\"personId\",\"personType\",\"Age\",\"Gender\",\"homeId\",\"homeSubId\","
						+ "\"homeLat\",\"homeLon\",\"diseasePhase\",\"workId\",\"schoolId\"");
		this.deadPersonWriter.flush();
	}

	private void writeDeadPersonLine(final Person person) {
		double time = this.model.getSimulator().getSimulatorTime();
		this.deadPersonWriter.print(time + "," + person.getId());
		this.deadPersonWriter.print(",\"" + person.getClass().getSimpleName() + "\"");
		this.deadPersonWriter.print("," + person.getAge());
		this.deadPersonWriter.print("," + (person.getGenderFemale() ? "\"F\"" : "\"M\""));
		this.deadPersonWriter.print("," + person.getHomeLocation().getId());
		this.deadPersonWriter.print("," + person.getHomeSubLocationIndex());
		Location homeLocation = this.model.getLocationMap().get(person.getHomeLocation().getId());
		this.deadPersonWriter.print("," + homeLocation.getLatitude());
		this.deadPersonWriter.print("," + homeLocation.getLongitude());
		this.deadPersonWriter.print(",\"" + person.getDiseasePhase().getName() + "\"");
		this.deadPersonWriter
				.print("," + (person instanceof Worker ? ((Worker) person).getWorkLocation().getId() : -1));
		this.deadPersonWriter
				.print("," + (person instanceof Student ? ((Student) person).getSchoolLocation().getId() : -1));
		this.deadPersonWriter.println();
		this.deadPersonWriter.flush();
	}

	/*
	 * ****************************** INFECTIONS PER PERSON TYPE
	 * ****************************************
	 */

	private void writeDayInfPersonTypeHeader() {
		this.dayInfPersonWriter.print("\"time\"");
		int ptSize = this.model.getPersonTypeList().size();
		for (int i = 0; i < ptSize; i++) {
			PersonType pt = this.model.getPersonTypeList().get(i);
			this.dayInfPersonWriter.print(",\"" + pt.getName() + "\"");
		}
		this.dayInfPersonWriter.println();
		this.dayInfPersonWriter.flush();
	}

	private void writeTotInfPersonTypeHeader() {
		this.totInfPersonWriter.print("\"time\"");
		int ptSize = this.model.getPersonTypeList().size();
		for (int i = 0; i < ptSize; i++) {
			PersonType pt = this.model.getPersonTypeList().get(i);
			this.totInfPersonWriter.print(",\"" + pt.getName() + "\"");
		}
		this.totInfPersonWriter.println();
		this.totInfPersonWriter.flush();
	}

	private void writeDayInfPersonTypeLine(final int[] nrs) {
		double time = this.model.getSimulator().getSimulatorTime();
		this.dayInfPersonWriter.print(Math.round(time));
		int ptSize = this.model.getPersonTypeList().size();
		for (int i = 0; i < ptSize; i++) {
			this.dayInfPersonWriter.print("," + nrs[i]);
		}
		this.dayInfPersonWriter.println();
		this.dayInfPersonWriter.flush();
	}

	private void writeTotInfPersonTypeLine(final int[] nrs) {
		double time = this.model.getSimulator().getSimulatorTime();
		this.totInfPersonWriter.print(Math.round(time));
		int ptSize = this.model.getPersonTypeList().size();
		for (int i = 0; i < ptSize; i++) {
			this.totInfPersonWriter.print("," + nrs[i]);
		}
		this.totInfPersonWriter.println();
		this.totInfPersonWriter.flush();
	}

	/*
	 * ****************************** INFECTIONS FROM PERSON TYPE TO PERSON TYPE
	 * *********************************
	 */

	private void writeDayInfPersonToPersonTypeHeader() {
		this.dayInfPersonToPersonWriter.print("\"time\",\"infecting_person_type\"");
		int ptSize = this.model.getPersonTypeList().size();
		for (int i = 0; i < ptSize; i++) {
			PersonType pt = this.model.getPersonTypeList().get(i);
			this.dayInfPersonToPersonWriter.print(",\"" + pt.getName() + "\"");
		}
		this.dayInfPersonToPersonWriter.println();
		this.dayInfPersonToPersonWriter.flush();
	}

	private void writeTotInfPersonToPersonTypeHeader() {
		this.totInfPersonToPersonWriter.print("\"time\",\"infecting_person_type\"");
		int ptSize = this.model.getPersonTypeList().size();
		for (int i = 0; i < ptSize; i++) {
			PersonType pt = this.model.getPersonTypeList().get(i);
			this.totInfPersonToPersonWriter.print(",\"" + pt.getName() + "\"");
		}
		this.totInfPersonToPersonWriter.println();
		this.totInfPersonToPersonWriter.flush();
	}

	private void writeDayInfPersonToPersonTypeLine(final int[] nrs) {
		double time = this.model.getSimulator().getSimulatorTime();
		String infectingPersonType = this.model.getPersonTypeList().get(nrs[0]).getName();
		this.dayInfPersonToPersonWriter.print(Math.round(time) + ",\"" + infectingPersonType + "\"");
		int ptSize = this.model.getPersonTypeList().size();
		for (int i = 0; i < ptSize; i++) {
			this.dayInfPersonToPersonWriter.print("," + nrs[i + 1]);
		}
		this.dayInfPersonToPersonWriter.println();
		this.dayInfPersonToPersonWriter.flush();
	}

	private void writeTotInfPersonToPersonTypeLine(final int[] nrs) {
		double time = this.model.getSimulator().getSimulatorTime();
		String infectingPersonType = this.model.getPersonTypeList().get(nrs[0]).getName();
		this.totInfPersonToPersonWriter.print(Math.round(time) + ",\"" + infectingPersonType + "\"");
		int ptSize = this.model.getPersonTypeList().size();
		for (int i = 0; i < ptSize; i++) {
			this.totInfPersonToPersonWriter.print("," + nrs[i + 1]);
		}
		this.totInfPersonToPersonWriter.println();
		this.totInfPersonToPersonWriter.flush();
	}

	/*
	 * ****************************** INFECTIONS FROM PERSON TYPE TO PERSON TYPE
	 * *********************************
	 */

	private void writeDayInfLocPersonToPersonTypeHeader() {
		this.dayInfLocPersonToPersonWriter.print("\"time\",\"location_type\",\"infecting_person_type\"");
		int ptSize = this.model.getPersonTypeList().size();
		for (int i = 0; i < ptSize; i++) {
			PersonType pt = this.model.getPersonTypeList().get(i);
			this.dayInfLocPersonToPersonWriter.print(",\"" + pt.getName() + "\"");
		}
		this.dayInfLocPersonToPersonWriter.println();
		this.dayInfLocPersonToPersonWriter.flush();
	}

	private void writeTotInfLocPersonToPersonTypeHeader() {
		this.totInfLocPersonToPersonWriter.print("\"time\",\"location_type\",\"infecting_person_type\"");
		int ptSize = this.model.getPersonTypeList().size();
		for (int i = 0; i < ptSize; i++) {
			PersonType pt = this.model.getPersonTypeList().get(i);
			this.totInfLocPersonToPersonWriter.print(",\"" + pt.getName() + "\"");
		}
		this.totInfLocPersonToPersonWriter.println();
		this.totInfLocPersonToPersonWriter.flush();
	}

	private void writeDayInfLocPersonToPersonTypeLine(final int[] nrs) {
		double time = this.model.getSimulator().getSimulatorTime();
		String locationType = this.model.getLocationTypeList().get(nrs[0]).getName();
		String infectingPersonType = this.model.getPersonTypeList().get(nrs[1]).getName();
		this.dayInfLocPersonToPersonWriter
				.print(Math.round(time) + ",\"" + locationType + "\",\"" + infectingPersonType + "\"");
		int ptSize = this.model.getPersonTypeList().size();
		for (int i = 0; i < ptSize; i++) {
			this.dayInfLocPersonToPersonWriter.print("," + nrs[i + 2]);
		}
		this.dayInfLocPersonToPersonWriter.println();
		this.dayInfLocPersonToPersonWriter.flush();
	}

	private void writeTotInfLocPersonToPersonTypeLine(final int[] nrs) {
		double time = this.model.getSimulator().getSimulatorTime();
		String locationType = this.model.getLocationTypeList().get(nrs[0]).getName();
		String infectingPersonType = this.model.getPersonTypeList().get(nrs[1]).getName();
		this.totInfLocPersonToPersonWriter
				.print(Math.round(time) + ",\"" + locationType + "\",\"" + infectingPersonType + "\"");
		int ptSize = this.model.getPersonTypeList().size();
		for (int i = 0; i < ptSize; i++) {
			this.totInfLocPersonToPersonWriter.print("," + nrs[i + 2]);
		}
		this.totInfLocPersonToPersonWriter.println();
		this.totInfLocPersonToPersonWriter.flush();
	}

	/*
	 * *************************************** INFECTIONS BY RATE
	 * ***************************************
	 */

	private void writeInfByRateHeader() {
		this.infByRateWriter
				.println("\"Time(h)\",\"personId\",\"personType\",\"Age\",\"Gender\",\"homeId\",\"homeSubId\","
						+ "\"homeLat\",\"homeLon\",\"diseasePhase\",\"workId\",\"schoolId\",\"infLocation\",\"duration\",\"infRate\"");
		this.infByRateWriter.flush();
	}

	private void writeInfByRateLine(final Object[] content) {
		// {exposedPerson, locationTypeId, duration, infectionRate}
		Person person = (Person) content[0];
		String infLocationType = this.model.getLocationTypeIndexMap().get((byte) (int) content[1]).getName();
		double duration = (double) content[2];
		double infectionRate = (double) content[3];
		double time = this.model.getSimulator().getSimulatorTime();
		this.infByRateWriter.print(time + "," + person.getId());
		this.infByRateWriter.print(",\"" + person.getClass().getSimpleName() + "\"");
		this.infByRateWriter.print("," + person.getAge());
		this.infByRateWriter.print("," + (person.getGenderFemale() ? "\"F\"" : "\"M\""));
		this.infByRateWriter.print("," + person.getHomeLocation().getId());
		this.infByRateWriter.print("," + person.getHomeSubLocationIndex());
		Location homeLocation = this.model.getLocationMap().get(person.getHomeLocation().getId());
		this.infByRateWriter.print("," + homeLocation.getLatitude());
		this.infByRateWriter.print("," + homeLocation.getLongitude());
		this.infByRateWriter.print(",\"" + person.getDiseasePhase().getName() + "\"");
		this.infByRateWriter.print("," + (person instanceof Worker ? ((Worker) person).getWorkLocation().getId() : -1));
		this.infByRateWriter
				.print("," + (person instanceof Student ? ((Student) person).getSchoolLocation().getId() : -1));
		this.infByRateWriter.print(",\"" + infLocationType + "\"");
		this.infByRateWriter.print("," + duration);
		this.infByRateWriter.print("," + infectionRate);
		this.infByRateWriter.println();
		this.infByRateWriter.flush();
	}

	private void writeInfByRateFactorHeader() {
		this.infByRateFactorWriter
				.println("\"Time(h)\",\"personId\",\"personType\",\"Age\",\"Gender\",\"homeId\",\"homeSubId\","
						+ "\"homeLat\",\"homeLon\",\"diseasePhase\",\"workId\",\"schoolId\","
						+ "\"infLocation\",\"duration\",\"infRateFactor\",\"refPersonType\",\"nrInfectedRef\",\"nrTotalRef\"");
		this.infByRateFactorWriter.flush();
	}

	private void writeInfByRateFactorLine(final Object[] content) {
		// {exposedPerson, locationTypeId, duration, infectionRateFactor, ref,
		// nrInfectedRef, nrTotalRef}
		Person person = (Person) content[0];
		String infLocationType = this.model.getLocationTypeIndexMap().get((byte) (int) content[1]).getName();
		double duration = (double) content[2];
		double infectionRateFactor = (double) content[3];
		String refPersonType = ((PersonType) content[4]).getName();
		int nrInfectedRef = (int) content[5];
		int nrTotalRef = (int) content[6];
		double time = this.model.getSimulator().getSimulatorTime();
		this.infByRateFactorWriter.print(time + "," + person.getId());
		this.infByRateFactorWriter.print(",\"" + person.getClass().getSimpleName() + "\"");
		this.infByRateFactorWriter.print("," + person.getAge());
		this.infByRateFactorWriter.print("," + (person.getGenderFemale() ? "\"F\"" : "\"M\""));
		this.infByRateFactorWriter.print("," + person.getHomeLocation().getId());
		this.infByRateFactorWriter.print("," + person.getHomeSubLocationIndex());
		Location homeLocation = this.model.getLocationMap().get(person.getHomeLocation().getId());
		this.infByRateFactorWriter.print("," + homeLocation.getLatitude());
		this.infByRateFactorWriter.print("," + homeLocation.getLongitude());
		this.infByRateFactorWriter.print(",\"" + person.getDiseasePhase().getName() + "\"");
		this.infByRateFactorWriter
				.print("," + (person instanceof Worker ? ((Worker) person).getWorkLocation().getId() : -1));
		this.infByRateFactorWriter
				.print("," + (person instanceof Student ? ((Student) person).getSchoolLocation().getId() : -1));
		this.infByRateFactorWriter.print(",\"" + infLocationType + "\"");
		this.infByRateFactorWriter.print("," + duration);
		this.infByRateFactorWriter.print("," + infectionRateFactor);
		this.infByRateFactorWriter.print(",\"" + refPersonType + "\"");
		this.infByRateFactorWriter.print("," + nrInfectedRef);
		this.infByRateFactorWriter.print("," + nrTotalRef);
		this.infByRateFactorWriter.println();
		this.infByRateFactorWriter.flush();
	}

	/*
	 * *************************************** ACTIVITY HOURS
	 * ***************************************
	 */

	private void writeDayActivityHeader() {
		this.dayActivityWriter.println("\"Time(h)\",\"activityType\",\"personType\",\"hours\"");
		this.dayActivityWriter.flush();
	}

	@SuppressWarnings("unchecked")
	private void writeDayActivityLine(final Object[] content) {
		// content = {dayHoursPerLocPerPerson, locationTypeToNrMap, personTypeToNrMap}
		List<TIntDoubleMap> dayHoursPerLocPerPerson = (List<TIntDoubleMap>) content[0];
		Map<String, Integer> locationTypeToNrMap = (Map<String, Integer>) content[1];
		Map<String, Integer> personTypeToNrMap = (Map<String, Integer>) content[2];
		double time = this.model.getSimulator().getSimulatorTime();
		for (Map.Entry<String, Integer> locEntry : locationTypeToNrMap.entrySet()) {
			String loc = locEntry.getKey();
			int locnr = locEntry.getValue();
			for (Map.Entry<String, Integer> ptEntry : personTypeToNrMap.entrySet()) {
				String pt = ptEntry.getKey();
				int ptnr = ptEntry.getValue();
				Double hrsD = dayHoursPerLocPerPerson.get(locnr).get(ptnr);
				double hrs = hrsD == null ? 0.0 : hrsD;
				this.dayActivityWriter.println(time + ",\"" + loc + "\",\"" + pt + "\"," + hrs);
			}
		}
		this.dayActivityWriter.flush();
	}

	private void writeTotActivityHeader() {
		this.totActivityWriter.println("\"Time(h)\",\"activityType\",\"personType\",\"hours\"");
		this.totActivityWriter.flush();
	}

	@SuppressWarnings("unchecked")
	private void writeTotActivityLine(final Object[] content) {
		// content = {totHoursPerLocPerPerson, locationTypeToNrMap, personTypeToNrMap}
		List<TIntDoubleMap> totHoursPerLocPerPerson = (List<TIntDoubleMap>) content[0];
		Map<String, Integer> locationTypeToNrMap = (Map<String, Integer>) content[1];
		Map<String, Integer> personTypeToNrMap = (Map<String, Integer>) content[2];
		double time = this.model.getSimulator().getSimulatorTime();
		for (Map.Entry<String, Integer> locEntry : locationTypeToNrMap.entrySet()) {
			String loc = locEntry.getKey();
			int locnr = locEntry.getValue();
			for (Map.Entry<String, Integer> ptEntry : personTypeToNrMap.entrySet()) {
				String pt = ptEntry.getKey();
				int ptnr = ptEntry.getValue();
				Double hrsD = totHoursPerLocPerPerson.get(locnr).get(ptnr);
				double hrs = hrsD == null ? 0.0 : hrsD;
				this.totActivityWriter.println(time + ",\"" + loc + "\",\"" + pt + "\"," + hrs);
			}
		}
		this.totActivityWriter.flush();
	}

	/** {@inheritDoc} */
	@SuppressWarnings("unchecked")
	@Override
	public void notify(final Event event) throws RemoteException {
		if (event.getType().equals(PersonMonitor.INFECT_ALL_LOCATIONTYPES_PER_HOUR_EVENT)) {
			writeInfectionLocationLine((Map<LocationType, Integer>) event.getContent());
		} else if (event.getType().equals(PersonMonitor.INFECT_AGE_PER_HOUR_EVENT)) {
			writeInfectionAgeLine((int[]) event.getContent());
		} else if (event.getType().equals(PersonMonitor.DEATHS_AGE_PER_DAY_EVENT)) {
			writeDeathsAgeLine((int[]) event.getContent());
		} else if (event.getType().equals(PersonMonitor.INFECTED_PERSON_EVENT)) {
			writeInfectedPersonLine((Object[]) event.getContent());
		} else if (event.getType().equals(PersonMonitor.DEAD_PERSON_EVENT)) {
			writeDeadPersonLine((Person) event.getContent());
		} else if (event.getType().equals(PersonMonitor.DAY_INFECTIONS_PERSON_TYPE)) {
			writeDayInfPersonTypeLine((int[]) event.getContent());
		} else if (event.getType().equals(PersonMonitor.TOT_INFECTIONS_PERSON_TYPE)) {
			writeTotInfPersonTypeLine((int[]) event.getContent());
		} else if (event.getType().equals(PersonMonitor.DAY_INFECTIONS_PERSON_TO_PERSON_TYPE)) {
			writeDayInfPersonToPersonTypeLine((int[]) event.getContent());
		} else if (event.getType().equals(PersonMonitor.TOT_INFECTIONS_PERSON_TO_PERSON_TYPE)) {
			writeTotInfPersonToPersonTypeLine((int[]) event.getContent());
		} else if (event.getType().equals(PersonMonitor.DAY_INFECTIONS_LOC_PERSON_TO_PERSON_TYPE)) {
			writeDayInfLocPersonToPersonTypeLine((int[]) event.getContent());
		} else if (event.getType().equals(PersonMonitor.TOT_INFECTIONS_LOC_PERSON_TO_PERSON_TYPE)) {
			writeTotInfLocPersonToPersonTypeLine((int[]) event.getContent());
		} else if (event.getType().equals(PersonMonitor.INFECTION_BY_RATE)) {
			writeInfByRateLine((Object[]) event.getContent());
		} else if (event.getType().equals(PersonMonitor.INFECTION_BY_RATE_FACTOR)) {
			writeInfByRateFactorLine((Object[]) event.getContent());
		} else if (event.getType().equals(ActivityMonitor.ACTIVITY_DAY_STATISTICS_EVENT)) {
			writeDayActivityLine((Object[]) event.getContent());
		} else if (event.getType().equals(ActivityMonitor.ACTIVITY_TOT_STATISTICS_EVENT)) {
			writeTotActivityLine((Object[]) event.getContent());
		}
	}

}

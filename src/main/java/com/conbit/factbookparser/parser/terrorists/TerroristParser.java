package com.conbit.factbookparser.parser.terrorists;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvMapReader;
import org.supercsv.io.ICsvMapReader;
import org.supercsv.prefs.CsvPreference;

import com.conbit.factbookparser.MyLogger;
import com.conbit.factbookparser.concept.Attack;
import com.conbit.factbookparser.owl.OwlHandler;

public class TerroristParser {
	private Logger logger = MyLogger.getInstance();
	private OwlHandler owlHandler;

	public static void main(String[] args) throws Exception {
		TerroristParser parser = new TerroristParser("/home/leendert/factbook-ont.owl");
		parser.readCsv("/home/leendert/globalterrorismdb_0611dist.uft8.csv");
	}
	
	public TerroristParser(String owlLocation) throws OWLOntologyCreationException{
		owlHandler =  new OwlHandler(owlLocation);
	}

	private void readCsv(String fileLocation) throws Exception {

		ICsvMapReader mapReader = null;
		try {
			mapReader = new CsvMapReader(new FileReader(fileLocation),
					CsvPreference.STANDARD_PREFERENCE);

			// the header columns are used as the keys to the Map
			final String[] header = mapReader.getHeader(true);
			final CellProcessor[] processors = getProcessors();

			Map<String, Object> attackMap;
			while ((attackMap = mapReader.read(header, processors)) != null) {
				handleEntry(attackMap);
			}
			owlHandler.save();

		} finally {
			if (mapReader != null) {
				mapReader.close();
			}
		}
	}

	private void handleEntry(Map<String, Object> attackMap) {
		String date = (String) attackMap.get("iday") + "/"+ attackMap.get("imonth")
				+"/"+ attackMap.get("iyear");
		String fatalities;
		if (attackMap.get("nkill") == null)
			fatalities = "Unknown";
		else
			fatalities = (String) attackMap.get("nkill");
		Attack attack = new Attack((String) attackMap.get("eventid"),
				fatalities, date);
		attack.addHasVictim((String) attackMap.get("targtype1_txt"));
		attack.addHasVictim((String) attackMap.get("targtype2_txt"));
		attack.addHasVictim((String) attackMap.get("targtype3_txt"));
		attack.addOfType((String) attackMap.get("attacktype1_txt"));
		attack.addOfType((String) attackMap.get("attacktype2_txt"));
		attack.addOfType((String) attackMap.get("attacktype3_txt"));
		attack.addPerpetrator((String) attackMap.get("gname"));
		attack.addPerpetrator((String) attackMap.get("gname2"));
		attack.addPerpetrator((String) attackMap.get("gname3"));
		attack.setCountry((String) attackMap.get("country_txt"));
		attack.writeToOwl(owlHandler);
	}

	private static CellProcessor[] getProcessors() {

		final CellProcessor[] processors = new CellProcessor[] { new NotNull(), // eventid
				new Optional(), // iyear
				new Optional(), // imonth
				new Optional(), // iday
				new Optional(), // approxdate
				new Optional(), // extended
				new Optional(), // resolution
				new Optional(), // country
				new Optional(), // country_txt
				new Optional(), // region
				new Optional(), // region_txt
				new Optional(), // provstate
				new Optional(), // city
				new Optional(), // vicinity
				new Optional(), // location
				new Optional(), // summary
				new Optional(), // crit1
				new Optional(), // crit2
				new Optional(), // crit3
				new Optional(), // doubtterr
				new Optional(), // alternative
				new Optional(), // alternative_txt
				new Optional(), // multiple
				new Optional(), // conflict
				new Optional(), // success
				new Optional(), // suicide
				new Optional(), // attacktype1
				new Optional(), // attacktype1_txt
				new Optional(), // attacktype2
				new Optional(), // attacktype2_txt
				new Optional(), // attacktype3
				new Optional(), // attacktype3_txt
				new Optional(), // targtype1
				new Optional(), // targtype1_txt
				new Optional(), // corp1
				new Optional(), // target1
				new Optional(), // natlty1
				new Optional(), // natlty1_txt
				new Optional(), // targtype2
				new Optional(), // targtype2_txt
				new Optional(), // corp2
				new Optional(), // target2
				new Optional(), // natlty2
				new Optional(), // natlty2_txt
				new Optional(), // targtype3
				new Optional(), // targtype3_txt
				new Optional(), // corp3
				new Optional(), // target3
				new Optional(), // natlty3
				new Optional(), // natlty3_txt
				new Optional(), // gname
				new Optional(), // gsubname
				new Optional(), // gname2
				new Optional(), // gsubname2
				new Optional(), // gname3
				new Optional(), // gsubname3
				new Optional(), // motive
				new Optional(), // guncertain1
				new Optional(), // guncertain2
				new Optional(), // guncertain3
				new Optional(), // nperps
				new Optional(), // nperpcap
				new Optional(), // claimed
				new Optional(), // claimmode
				new Optional(), // claimmode_txt
				new Optional(), // claimconf
				new Optional(), // claim2
				new Optional(), // claimmode2
				new Optional(), // claimmode2_txt
				new Optional(), // claimconf2
				new Optional(), // claim3
				new Optional(), // claimmode3
				new Optional(), // claimmode3_txt
				new Optional(), // claimconf3
				new Optional(), // compclaim
				new Optional(), // weaptype1
				new Optional(), // weaptype1_txt
				new Optional(), // weapsubtype1
				new Optional(), // weapsubtype1_txt
				new Optional(), // weaptype2
				new Optional(), // weaptype2_txt
				new Optional(), // weapsubtype2
				new Optional(), // weapsubtype2_txt
				new Optional(), // weaptype3
				new Optional(), // weaptype3_txt
				new Optional(), // weapsubtype3
				new Optional(), // weapsubtype3_txt
				new Optional(), // weaptype4
				new Optional(), // weaptype4_txt
				new Optional(), // weapsubtype4
				new Optional(), // weapsubtype4_txt
				new Optional(), // weapdetail
				new Optional(), // nkill
				new Optional(), // nkillus
				new Optional(), // nkillter
				new Optional(), // nwound
				new Optional(), // nwoundus
				new Optional(), // nwoundte
				new Optional(), // property
				new Optional(), // propextent
				new Optional(), // propextent_txt
				new Optional(), // propvalue
				new Optional(), // propcomment
				new Optional(), // ishostkid
				new Optional(), // nhostkid
				new Optional(), // nhostkidus
				new Optional(), // nhours
				new Optional(), // ndays
				new Optional(), // divert
				new Optional(), // kidhijcountry
				new Optional(), // ransom
				new Optional(), // ransomamt
				new Optional(), // ransomamtus
				new Optional(), // ransompaid
				new Optional(), // ransompaidus
				new Optional(), // ransomnote
				new Optional(), // hostkidoutcome
				new Optional(), // hostkidoutcome_txt
				new Optional(), // nreleased
				new Optional(), // addnotes
				new Optional(), // scite1
				new Optional(), // scite2
				new Optional(), // scite3
				new Optional(), // dbsource
		};
		return processors;
	}
}

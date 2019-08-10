package com.tobprecise.demo.entities.clinical;

import java.util.Date;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public class Demography {
	@Getter @Setter private String givenName;
	@Getter @Setter private String familyName;
	@Getter @Setter private Gender gender;
	@Getter @Setter private Date dateOfBirth;
	@Getter @Setter private Boolean isDeceased;
	
	public enum Gender {
		MALE, FEMALE, UNKNOWN
	}
}

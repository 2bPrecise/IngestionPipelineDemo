package com.tobprecise.demo.entities.clinical;

import java.util.List;

import org.bson.types.ObjectId;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Field;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Index;
import dev.morphia.annotations.Indexes;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@EqualsAndHashCode
@Entity("patients")
@Indexes(
	    @Index(fields = @Field("patientId"))
	)
public class Patient {
    @Id @Getter @Setter private ObjectId id;
	@Getter @Setter private String patientId;
	@Getter @Setter private Demography demography;
	@Getter @Setter private List<Medication> medications;
}

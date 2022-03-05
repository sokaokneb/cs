package hu.benkoa.cs.repo;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "events")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
public class Event {

    @Id
    private String id;
    private Long duration;
    private String type;
    private String host;
    private Boolean alert;

}

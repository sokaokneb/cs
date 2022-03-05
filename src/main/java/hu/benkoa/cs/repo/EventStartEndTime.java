package hu.benkoa.cs.repo;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "eventstartendtimes")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
public class EventStartEndTime {

    @Id
    private String id;
    private Long start;
    private Long end;
    private String type;
    private String host;

}

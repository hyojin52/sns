package com.fastcampus.sns.model.entity;

import com.fastcampus.sns.model.AlarmArgs;
import com.fastcampus.sns.model.AlarmType;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.Data;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.Instant;

@Entity
@Table(name = "\"alarm\"", indexes = {
        @Index(name="user_id_idx", columnList = "user_id")
})
@Data
@TypeDef(name="jsonb", typeClass= JsonBinaryType.class)
@SQLDelete(sql = "UPDATE \"alarm\" SET deleted_at = NOW() where id=?")
@Where(clause = "deleted_at is NULL")
public class AlarmEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;
  
  @ManyToOne
  @JoinColumn(name = "user_id")
  private UserEntity user;
  
  @Enumerated(EnumType.STRING)
  private AlarmType alarmType;
  
  @Type(type="jsonb")
  @Column(columnDefinition = "json")
  private AlarmArgs args;
  
  @Column(name="registered_at")
  private Timestamp registeredAt;
  
  @Column(name="updated_at")
  private Timestamp updatedAt;
  
  @Column(name="deleted_at")
  private Timestamp deletedAt;
  
  @PrePersist
  void registeredAt() {this.registeredAt = Timestamp.from(Instant.now());}
  
  @PreUpdate
  void updatedAt() {this.updatedAt = Timestamp.from(Instant.now());}
  
  public static AlarmEntity of(UserEntity userEntity, AlarmType alarmType, AlarmArgs args) {
    AlarmEntity alarmEntity = new AlarmEntity();
    alarmEntity.setAlarmType(alarmType);
    alarmEntity.setUser(userEntity);
    alarmEntity.setArgs(args);
    return alarmEntity;
  }
  
}

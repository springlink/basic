package sourcefx.core;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.Version;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.AfterDomainEventPublication;
import org.springframework.data.domain.DomainEvents;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.util.Assert;

import com.google.common.collect.Lists;

import lombok.Getter;
import sourcefx.util.Snowflake;

@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
@SuppressWarnings("serial")
public abstract class BaseEntity implements Serializable {
	@Getter
	@Id
	private Long id;

	@Getter
	@CreatedDate
	private LocalDateTime createdDate;

	@Getter
	@CreatedBy
	private Long createdBy;

	@Getter
	@LastModifiedDate
	private LocalDateTime lastModifiedDate;

	@Getter
	@LastModifiedBy
	private Long lastModifiedBy;

	@Getter
	private boolean deleted;

	@Version
	private Long version = 0L;

	@Transient
	private transient final List<Object> domainEvents = Lists.newArrayListWithCapacity(1);

	public void markDeleted() {
		this.deleted = true;
	}

	@PrePersist
	protected void prePersist() {
		if (id == null) {
			id = Snowflake.getInstance().next();
		}
	}

	protected <T> T registerEvent(T event) {
		Assert.notNull(event, "Domain event must not be null!");
		this.domainEvents.add(event);
		return event;
	}

	@AfterDomainEventPublication
	protected void clearDomainEvents() {
		this.domainEvents.clear();
	}

	@DomainEvents
	protected Collection<Object> domainEvents() {
		return Collections.unmodifiableList(domainEvents);
	}
}

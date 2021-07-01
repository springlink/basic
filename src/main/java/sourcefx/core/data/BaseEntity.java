package sourcefx.core.data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Version;

import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.AfterDomainEventPublication;
import org.springframework.data.domain.DomainEvents;

import com.google.common.collect.Lists;

import lombok.Getter;
import lombok.NonNull;
import sourcefx.core.AppUtils;
import sourcefx.util.Snowflake;

@MappedSuperclass
@SuppressWarnings("serial")
public abstract class BaseEntity implements Serializable {
	@Getter
	@Id
	private Long id;

	@Getter
	private LocalDateTime createdDate;

	@Getter
	private Long createdBy;

	@Getter
	private LocalDateTime lastModifiedDate;

	@Getter
	private Long lastModifiedBy;

	@Getter
	@Deleted
	private boolean deleted;

	@Version
	private long version;

	@Transient
	private transient final List<Object> domainEvents = Lists.newArrayListWithCapacity(1);

	public void markDeleted() {
		this.deleted = true;
	}

	@PrePersist
	protected void prePersist() {
		if (id == null) {
			LocalDateTime now = LocalDateTime.now();
			Long userId = AppUtils.getCurrentUserId().orElse(null);
			id = Snowflake.getInstance().next();
			createdDate = now;
			createdBy = userId;
			lastModifiedDate = now;
			lastModifiedBy = userId;
		}
	}

	@PreUpdate
	protected void preUpdate() {
		lastModifiedDate = LocalDateTime.now();
		lastModifiedBy = AppUtils.getCurrentUserId().orElse(null);
	}

	protected <T> T registerEvent(@NonNull T event) {
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

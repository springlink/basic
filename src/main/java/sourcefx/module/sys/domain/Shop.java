package sourcefx.module.sys.domain;

import javax.persistence.Entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sourcefx.core.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Shop extends BaseEntity {
	private static final long serialVersionUID = 1L;

	private String name;

	private String address;

	public Shop(String name, String address) {
		this.name = name;
		this.address = address;
	}
}

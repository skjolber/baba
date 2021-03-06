/*
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *   https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 *
 */

package no.rutebanken.baba.provider.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
public class Provider {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    public Long id;
    public String name;
    public String sftpAccount;
    @OneToOne(cascade = {CascadeType.ALL})
    public ChouetteInfo chouetteInfo;

    public Provider(){}

    public Provider(Long id, String name, String sftpAccount, ChouetteInfo chouetteInfo) {
        this.id = id;
        this.name = name;
        this.sftpAccount = sftpAccount;
        this.chouetteInfo = chouetteInfo;
    }

    @Override
    public String toString() {
        return "Provider{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", sftpAccount='" + sftpAccount + '\'' +
                ", chouetteInfo=" + chouetteInfo +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Provider provider = (Provider) o;

        if (id != null ? !id.equals(provider.id) : provider.id != null) return false;
        if (name != null ? !name.equals(provider.name) : provider.name != null) return false;
        if (sftpAccount != null ? !sftpAccount.equals(provider.sftpAccount) : provider.sftpAccount != null)
            return false;
        return chouetteInfo != null ? chouetteInfo.equals(provider.chouetteInfo) : provider.chouetteInfo == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (sftpAccount != null ? sftpAccount.hashCode() : 0);
        result = 31 * result + (chouetteInfo != null ? chouetteInfo.hashCode() : 0);
        return result;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSftpAccount() {
        return sftpAccount;
    }

    public void setSftpAccount(String sftpAccount) {
        this.sftpAccount = sftpAccount;
    }

    public ChouetteInfo getChouetteInfo() {
        return chouetteInfo;
    }

    public void setChouetteInfo(ChouetteInfo chouetteInfo) {
        this.chouetteInfo = chouetteInfo;
    }
}

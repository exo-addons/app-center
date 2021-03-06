package org.exoplatform.appcenter.entity;

import javax.persistence.*;

import org.exoplatform.commons.api.persistence.ExoEntity;

/**
 * @author Ayoub Zayati
 */
@Entity(name = "FavoriteApplicationEntity")
@ExoEntity
@Table(name = "AC_FAVORITE_APPLICATION")
@NamedQueries({
    @NamedQuery(name = "FavoriteApplicationEntity.getFavoriteAppByUserNameAndAppId", query = "SELECT distinct(favoriteApp) FROM FavoriteApplicationEntity favoriteApp "
        + " WHERE favoriteApp.application.id = :applicationId AND favoriteApp.userName = :userName"),
    @NamedQuery(name = "FavoriteApplicationEntity.getFavoriteAppsByAppId", query = "SELECT distinct(favoriteApp) FROM FavoriteApplicationEntity favoriteApp "
        + " WHERE favoriteApp.application.id = :applicationId"),
    @NamedQuery(name = "FavoriteApplicationEntity.countFavoritesByUser", query = "SELECT count(*) FROM FavoriteApplicationEntity favoriteApp "
        + " WHERE favoriteApp.userName = :userName"),

})
public class FavoriteApplicationEntity {

  @Id
  @SequenceGenerator(name = "SEQ_FAVORITE_APPLICATION_ID", sequenceName = "SEQFAVORITE_APPLICATION_ID")
  @GeneratedValue(strategy = GenerationType.AUTO, generator = "SEQ_FAVORITE_APPLICATION_ID")
  @Column(name = "ID")
  private Long        id;

  @ManyToOne
  @JoinColumn(name = "APPLICATION_ID")
  private ApplicationEntity application;

  @Column(name = "USER_NAME")
  private String      userName;

  public FavoriteApplicationEntity() {
  }

  public FavoriteApplicationEntity(ApplicationEntity application, String userName) {
    this.application = application;
    this.userName = userName;
  }

  /**
   * @return the id
   */
  public Long getId() {
    return id;
  }

  /**
   * @param id the id to set
   */
  public void setId(Long id) {
    this.id = id;
  }

  /**
   * @return the application
   */
  public ApplicationEntity getApplication() {
    return application;
  }

  /**
   * @param application the application to set
   */
  public void setApplication(ApplicationEntity application) {
    this.application = application;
  }

  /**
   * @return the userName
   */
  public String getUserName() {
    return userName;
  }

  /**
   * @param userName the userName to set
   */
  public void setUserName(String userName) {
    this.userName = userName;
  }
}

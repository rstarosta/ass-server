package ass.starorad.semestralproject.server.impl;

public class AuthorizationData {
  private final String user;
  private final String password;

  public AuthorizationData(String user, String password) {
    this.user = user;
    this.password = password;
  }

  public String getUser() {
    return user;
  }

  public String getPassword() {
    return password;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    AuthorizationData that = (AuthorizationData) o;

    if (!user.equals(that.user)) {
      return false;
    }
    return password.equals(that.password);
  }

  @Override
  public int hashCode() {
    int result = user.hashCode();
    result = 31 * result + password.hashCode();
    return result;
  }
}

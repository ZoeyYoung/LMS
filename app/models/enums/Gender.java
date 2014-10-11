package models.enums;

public enum Gender {
    MAN("男"), FEMALE("女");

    private final String gender;

    Gender(final String gender) {
        this.gender = gender;
    }

    public String gender() {
        return gender;
    }
}

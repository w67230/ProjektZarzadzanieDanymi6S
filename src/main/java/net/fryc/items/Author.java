package net.fryc.items;

import org.springframework.lang.Nullable;

import java.util.Date;

public record Author(int id, String firstName, String lastName, Date birthDate, @Nullable Date deathDate) {
}

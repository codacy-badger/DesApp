package unq.edu.tpi.desapp.model;

import org.junit.Test;
import unq.edu.tpi.desapp.model.builders.DonationBuilder;
import unq.edu.tpi.desapp.model.builders.LocationBuilder;
import unq.edu.tpi.desapp.model.builders.ProjectBuilder;
import unq.edu.tpi.desapp.model.builders.UserBuilder;
import unq.edu.tpi.desapp.model.exceptions.EndDateMustBeAfterStartDate;
import unq.edu.tpi.desapp.model.exceptions.IntegerMustBePositive;
import unq.edu.tpi.desapp.model.exceptions.InvalidFactor;
import unq.edu.tpi.desapp.model.exceptions.InvalidMinClosePercentage;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;

public class ProjectTest {

    @Test
    public void projectsAreCreatedWith0FundsInitially() throws EndDateMustBeAfterStartDate, InvalidMinClosePercentage, InvalidFactor, IntegerMustBePositive {
        Project newProject = ProjectBuilder.aProject().build();
        assertEquals((Integer)0, newProject.getRaisedFunds());
    }

    @Test
    public void projectsAreCreatedWith1000FactorInitiallyByDefault() throws EndDateMustBeAfterStartDate, InvalidMinClosePercentage, InvalidFactor, IntegerMustBePositive {
        Project newProject = ProjectBuilder.aProject().build();
        assertEquals((Integer)1000, newProject.getFactor());
    }

    @Test
    public void projectsAreCreatedWith100MinimumPercentageByDefault() throws EndDateMustBeAfterStartDate, InvalidMinClosePercentage, InvalidFactor, IntegerMustBePositive {
        Project newProject = ProjectBuilder.aProject().build();
        assertEquals((Float)100.0f, newProject.getMinClosePercentage());
    }

    @Test(expected = EndDateMustBeAfterStartDate.class)
    public void cantCreateProjectWithEndDateBeforeStartDate() throws EndDateMustBeAfterStartDate, InvalidMinClosePercentage, InvalidFactor, IntegerMustBePositive {
        ProjectBuilder.aProject().withEndDate(LocalDate.of(1880, 5, 18)).build();
    }

    @Test(expected = InvalidFactor.class)
    public void cantCreateProjectWithNegativeFactor() throws EndDateMustBeAfterStartDate, InvalidMinClosePercentage, InvalidFactor, IntegerMustBePositive {
        ProjectBuilder.aProject().withFactor(-2).build();
    }

    @Test(expected = InvalidFactor.class)
    public void cantCreateProjectWithFactorHigherThan100000() throws EndDateMustBeAfterStartDate, InvalidMinClosePercentage, InvalidFactor, IntegerMustBePositive {
        ProjectBuilder.aProject().withFactor(100001).build();
    }

    @Test(expected = InvalidMinClosePercentage.class)
    public void cantCreateProjectWithMinClosePercentageLowerThan50() throws EndDateMustBeAfterStartDate, InvalidMinClosePercentage, InvalidFactor, IntegerMustBePositive {
        ProjectBuilder.aProject().withMinClosePercentage(49.9f).build();
    }

    @Test(expected = InvalidMinClosePercentage.class)
    public void cantCreateProjectWithMinClosePercentageHigherThan100() throws EndDateMustBeAfterStartDate, InvalidMinClosePercentage, InvalidFactor, IntegerMustBePositive {
        ProjectBuilder.aProject().withMinClosePercentage(100.1f).build();
    }

    @Test
    public void missingPercentageToCompleteIsDirectlyRelatedToMinClosePercentage() throws InvalidMinClosePercentage, EndDateMustBeAfterStartDate, InvalidFactor, IntegerMustBePositive {
        Project sampleProject = ProjectBuilder.aProject().build();
        User sampleUser = UserBuilder.aUser().build();
        sampleProject.donate(250, "Donating 25% of the total $1000", sampleUser);
        assertEquals((Float)75.0f, sampleProject.missingPercentageToComplete());
        Project projectWithHalfMinPercentage = ProjectBuilder.aProject().withMinClosePercentage(50.0f).build();
        projectWithHalfMinPercentage.donate(250, "Donating 25% of the total $1000", sampleUser);
        assertEquals((Float)50.0f, projectWithHalfMinPercentage.missingPercentageToComplete());
    }

    @Test
    public void addingParticipantsIncreaseParticipantsAmount() throws InvalidMinClosePercentage, EndDateMustBeAfterStartDate, InvalidFactor, IntegerMustBePositive {
        Project sampleProject = ProjectBuilder.aProject().build();
        assertEquals((Integer)0, sampleProject.participantsAmount());
        User sampleUser1 = UserBuilder.aUser().build();
        User sampleUser2 = UserBuilder.aUser().build();
        sampleProject.addParticipant(sampleUser1);
        assertEquals((Integer)1, sampleProject.participantsAmount());
        sampleProject.addParticipant(sampleUser2);
        assertEquals((Integer)2, sampleProject.participantsAmount());
    }

    @Test
    public void participantsAmountIs1ForEveryUniqueParticipant() throws InvalidMinClosePercentage, EndDateMustBeAfterStartDate, InvalidFactor, IntegerMustBePositive {
        Project sampleProject = ProjectBuilder.aProject().build();
        User sampleUser = UserBuilder.aUser().build();
        sampleProject.addParticipant(sampleUser);
        sampleProject.addParticipant(sampleUser);
        sampleProject.addParticipant(sampleUser);
        assertEquals((Integer)1, sampleProject.participantsAmount());
    }

    @Test
    public void accumulatedValuePercentageIgnoresMinClosePercentage() throws InvalidMinClosePercentage, EndDateMustBeAfterStartDate, InvalidFactor, IntegerMustBePositive {
        Project sampleProjectWithMinClosePercentOf80 = ProjectBuilder.aProject().withMinClosePercentage(80.0f).build();
        assertEquals((Float)0.0f, sampleProjectWithMinClosePercentOf80.accumulatedValuePercentage());
        User sampleUser = UserBuilder.aUser().build();
        sampleProjectWithMinClosePercentOf80.donate(500, "50% donation ignoring minimum close percentage", sampleUser);
        assertEquals((Float)50.0f, sampleProjectWithMinClosePercentOf80.accumulatedValuePercentage());
    }

    @Test(expected = IntegerMustBePositive.class)
    public void cantDonateNegativeAmount() throws IntegerMustBePositive, EndDateMustBeAfterStartDate, InvalidMinClosePercentage, InvalidFactor {
        Project sampleProject = ProjectBuilder.aProject().build();
        User sampleUser = UserBuilder.aUser().build();
        sampleProject.donate(-16, "A negative donation", sampleUser);
    }

    @Test
    public void donate100Currency2TimesToRaiseFundsAppropriately() throws IntegerMustBePositive, EndDateMustBeAfterStartDate, InvalidMinClosePercentage, InvalidFactor {
        Project sampleProject = ProjectBuilder.aProject().build();
        User sampleUser = UserBuilder.aUser().build();
        sampleProject.donate(100, "My first donation", sampleUser);
        assertEquals((Integer)100, sampleProject.getRaisedFunds());
        sampleProject.donate(100, "My second donation", sampleUser);
        assertEquals((Integer)200, sampleProject.getRaisedFunds());
    }

    @Test
    public void donatingAddsParticipantsToProject() throws IntegerMustBePositive, InvalidMinClosePercentage, EndDateMustBeAfterStartDate, InvalidFactor {
        Project sampleProject = ProjectBuilder.aProject().build();
        User sampleUser = UserBuilder.aUser().build();
        assertEquals((Integer)0, sampleProject.participantsAmount());
        sampleProject.donate(100, "My first donation", sampleUser);
        assertEquals((Integer)1, sampleProject.participantsAmount());
        sampleProject.donate(150, "My second donation", sampleUser);
        assertEquals((Integer)1, sampleProject.participantsAmount());
        User sampleUser2 = UserBuilder.aUser().build();
        sampleProject.donate(42, "My second donation", sampleUser2);
        assertEquals((Integer)2, sampleProject.participantsAmount());
    }

    @Test
    public void projectsAreInPlannedStateInitially() throws IntegerMustBePositive, InvalidMinClosePercentage, EndDateMustBeAfterStartDate, InvalidFactor {
        Project sampleProject = ProjectBuilder.aProject().build();
        assertEquals("En Planificacion", sampleProject.getState());
    }

    @Test
    public void projectsChangeToSuspendedStateIfTriedToCompleteAfterEndDate() throws IntegerMustBePositive, InvalidMinClosePercentage, EndDateMustBeAfterStartDate, InvalidFactor {
        Project sampleProject = ProjectBuilder.aProject().withStartDate(LocalDate.of(1990, 12, 22)).withEndDate(LocalDate.of(2000, 12, 22)).build();
        assertEquals("En Planificacion", sampleProject.getState());
        sampleProject.completeProject();
        assertEquals("Suspendido", sampleProject.getState());
    }

    @Test
    public void projectsChangeToConnectedStateOnlyIfTriedToCompleteWithLessThanOrEqualTo0PercentFundsMissingAndBeforeEndDate() throws IntegerMustBePositive, InvalidMinClosePercentage, EndDateMustBeAfterStartDate, InvalidFactor {
        Project sampleProject = ProjectBuilder.aProject().withEndDate(LocalDate.of(2055, 12, 22)).build();
        User sampleUser = UserBuilder.aUser().build();
        assertEquals((Float)100.0f, sampleProject.missingPercentageToComplete());
        sampleProject.completeProject();
        assertEquals("En Planificacion", sampleProject.getState());
        sampleProject.donate(1000, "full clear percentage donation", sampleUser);
        assertEquals((Float)0.0f, sampleProject.missingPercentageToComplete());
        sampleProject.completeProject();
        assertEquals("Conectado", sampleProject.getState());
    }

    @Test
    public void connectedProjectsDoNothingWhenTriedToDonateToThem() throws IntegerMustBePositive, InvalidMinClosePercentage, EndDateMustBeAfterStartDate, InvalidFactor {
        Project connectedProject = ProjectBuilder.aProject().build();
        connectedProject.setProjectState(new Connected());
        User sampleUser = UserBuilder.aUser().build();
        connectedProject.donate(15000, "This donation wont count", sampleUser);
        assertEquals((Integer) 0, connectedProject.getRaisedFunds());
        assertEquals((Integer) 0, connectedProject.participantsAmount());
    }

    @Test
    public void suspendedProjectsDoNothingWhenTriedToDonateToThem() throws IntegerMustBePositive, InvalidMinClosePercentage, EndDateMustBeAfterStartDate, InvalidFactor {
        Project suspendedProject = ProjectBuilder.aProject().build();
        suspendedProject.setProjectState(new Suspended());
        User sampleUser = UserBuilder.aUser().build();
        suspendedProject.donate(3333, "This donation wont count", sampleUser);
        assertEquals((Integer) 0, suspendedProject.getRaisedFunds());
        assertEquals((Integer) 0, suspendedProject.participantsAmount());
    }

    @Test
    public void gettersAndSettersForCoverage() throws IntegerMustBePositive, InvalidMinClosePercentage, EndDateMustBeAfterStartDate, InvalidFactor {
        Project sampleProject = ProjectBuilder.aProject().build();
        assertEquals(0, sampleProject.getDonations().size());
        sampleProject.addDonation(DonationBuilder.aDonation().build());
        assertEquals(1, sampleProject.getDonations().size());

        sampleProject.setName("Cool Project Name");
        assertEquals("Cool Project Name", sampleProject.getName());

        sampleProject.setStartDate(LocalDate.of(2020,1,27));
        assertEquals(LocalDate.of(2020,1,27), sampleProject.getStartDate());

        sampleProject.setEndDate(LocalDate.of(2022, 8, 17));
        assertEquals(LocalDate.of(2022, 8, 17), sampleProject.getEndDate());

        sampleProject.setRaisedFunds(666);
        assertEquals((Integer) 666, sampleProject.getRaisedFunds());

        Location sampleLocation = LocationBuilder.aLocation().build();
        sampleProject.setLocation(sampleLocation);
        assertEquals(sampleLocation, sampleProject.getLocation());

        sampleProject.setFactor(5555);
        assertEquals((Integer) 5555, sampleProject.getFactor());

        sampleProject.setMinClosePercentage(88.0f);
        assertEquals((Float) 88.0f, sampleProject.getMinClosePercentage());
    }

    @Test(expected = EndDateMustBeAfterStartDate.class)
    public void cantSetEndDateBeforeStartDate() throws IntegerMustBePositive, InvalidMinClosePercentage, EndDateMustBeAfterStartDate, InvalidFactor {
        Project sampleProject = ProjectBuilder.aProject().withStartDate(LocalDate.of(2020, 4, 19)).withEndDate(LocalDate.of(2222, 12, 22)).build();
        sampleProject.setEndDate(LocalDate.of(2019, 7, 15));
    }

    @Test(expected = IntegerMustBePositive.class)
    public void cantSetNegativeRaisedFunds() throws IntegerMustBePositive, InvalidMinClosePercentage, EndDateMustBeAfterStartDate, InvalidFactor {
        Project sampleProject = ProjectBuilder.aProject().build();
        sampleProject.setRaisedFunds(-33);
    }

    @Test(expected = IntegerMustBePositive.class)
    public void cantAddNegativeFunds() throws IntegerMustBePositive, InvalidMinClosePercentage, EndDateMustBeAfterStartDate, InvalidFactor {
        Project sampleProject = ProjectBuilder.aProject().build();
        sampleProject.addFunds(-33);
    }

    @Test(expected = InvalidFactor.class)
    public void cantSetNegativeFactor() throws IntegerMustBePositive, InvalidMinClosePercentage, EndDateMustBeAfterStartDate, InvalidFactor {
        Project sampleProject = ProjectBuilder.aProject().build();
        sampleProject.setFactor(-11);
    }

    @Test(expected = InvalidFactor.class)
    public void cantSetFactorHigherThan100000() throws IntegerMustBePositive, InvalidMinClosePercentage, EndDateMustBeAfterStartDate, InvalidFactor {
        Project sampleProject = ProjectBuilder.aProject().build();
        sampleProject.setFactor(111111);
    }

    @Test(expected = InvalidMinClosePercentage.class)
    public void cantSetMinClosePercentageLowerThan50Percent() throws IntegerMustBePositive, InvalidMinClosePercentage, EndDateMustBeAfterStartDate, InvalidFactor {
        Project sampleProject = ProjectBuilder.aProject().build();
        sampleProject.setMinClosePercentage(44.4f);
    }

    @Test(expected = InvalidMinClosePercentage.class)
    public void cantSetMinClosePercentageHigherThan100Percent() throws IntegerMustBePositive, InvalidMinClosePercentage, EndDateMustBeAfterStartDate, InvalidFactor {
        Project sampleProject = ProjectBuilder.aProject().build();
        sampleProject.setMinClosePercentage(115.5f);
    }
}
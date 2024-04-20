package me.blunivers.identity;

import me.blunivers.identity.Environment.CustomBlockInstance;
import me.blunivers.identity.Health.Conditions.*;
import me.blunivers.identity.Health.Conditions.Illnesses.Illness;
import me.blunivers.identity.Jobs.JobType;
import me.blunivers.identity.Jobs.JobInstance;
import org.bukkit.entity.Player;
import org.joml.Vector3i;

import java.sql.*;
import java.util.ArrayList;

public final class Database {

    private final Connection connection;

    public Database(String path) throws SQLException{

        connection = DriverManager.getConnection("jdbc:sqlite:" + path);
        try (Statement statement = connection.createStatement();){
            statement.execute("""
                CREATE TABLE IF NOT EXISTS players (
                player_id STRING PRIMARY KEY,
                online BOOLEAN NOT NULL
                );
            """);
            statement.execute("""
                CREATE TABLE IF NOT EXISTS jobs (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                job_id TEXT NOT NULL,
                player_id TEXT NOT NULL,
                level INTEGER NOT NULL DEFAULT 1,
                progression INTEGER NOT NULL DEFAULT 0,
                UNIQUE(player_id, job_id)
                );
            """);
            statement.execute("""
                CREATE TABLE IF NOT EXISTS environment_blocks (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                block_id TEXT NOT NULL,
                x INTEGER NOT NULL,
                y INTEGER NOT NULL,
                z INTEGER NOT NULL,
                world STRING NOT NULL DEFAULT 'world',
                UNIQUE(x, y, z, world)
                );
            """);
            statement.execute("""
                CREATE TABLE IF NOT EXISTS health_conditions (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                condition_id TEXT NOT NULL,
                player_id TEXT NOT NULL,
                stage INTEGER NOT NULL DEFAULT 1,
                time_before_next_stage INTEGER NOT NULL DEFAULT -1111,
                hidden BOOLEAN NOT NULL DEFAULT TRUE,
                UNIQUE(player_id, condition_id)
                );
            """);
            statement.execute("""
                CREATE TABLE IF NOT EXISTS health_medications (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                medication_id TEXT NOT NULL,
                player_id TEXT NOT NULL,
                time_before_expiration INTEGER NOT NULL DEFAULT 72000,
                expired BOOLEAN NOT NULL DEFAULT FALSE,
                UNIQUE(player_id, medication_id)
                );
            """);
        }
    }

    public void closeConnection() throws SQLException{
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    public void players_join(Player player) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT OR REPLACE INTO players (player_id, online) VALUES (?, TRUE)")) {
            preparedStatement.setString(1, player.getUniqueId().toString());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to execute players_join! " + e.getMessage());
        }
    }

    public void players_leave(Player player) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("UPDATE players SET ONLINE = FALSE WHERE player_id = ?")) {
            preparedStatement.setString(1, player.getUniqueId().toString());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to execute players_leave! " + e.getMessage());
        }
    }
    public void players_reset() {
        try (PreparedStatement preparedStatement = connection.prepareStatement("UPDATE players SET ONLINE = FALSE")) {
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to execute players_reset! " + e.getMessage());
        }
    }
    public void jobs_employPlayer(Player player, JobType jobType) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO jobs (job_id, player_id) VALUES (?, ?)")) {
            preparedStatement.setString(1, jobType.name);
            preparedStatement.setString(2, player.getUniqueId().toString());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to execute jobs_employPlayer! " + e.getMessage());
        }
    }
    public void jobs_leaveJob(Player player, JobType jobType) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM jobs WHERE job_id = ? AND player_id = ?")) {
            preparedStatement.setString(1, jobType.name);
            preparedStatement.setString(2, player.getUniqueId().toString());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to execute jobs_leaveJob! " + e.getMessage());
        }
    }
    public void jobs_updateProgress(Player player, JobType jobType, int level, int progression) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("UPDATE jobs SET level = ?, progression = ? WHERE job_id = ? AND player_id = ?")) {
            preparedStatement.setInt(1, level);
            preparedStatement.setInt(2, progression);
            preparedStatement.setString(3, jobType.name);
            preparedStatement.setString(4, player.getUniqueId().toString());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to execute jobs_updateProgress! " + e.getMessage());
        }
    }

    public ArrayList<JobType> jobs_getJobTypes(Player player) {
        ArrayList<JobType> jobTypes = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT job_id FROM jobs WHERE player_id = ?")) {
            preparedStatement.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                jobTypes.add(JobType.get(resultSet.getString("job_id")));
            }
        } catch (SQLException e) {
            System.out.println("Failed to execute jobs_getJobTypes! " + e.getMessage());
        }
        return jobTypes;
    }
    public ArrayList<JobInstance> jobs_getJobInstances(Player player) {
        ArrayList<JobInstance> jobInstances = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM jobs WHERE player_id = ?")) {
            preparedStatement.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                jobInstances.add(new JobInstance(resultSet.getInt("id"), JobType.get(resultSet.getString("job_id")), resultSet.getString("player_id"), resultSet.getInt("level"), resultSet.getInt("progression")));
            }
        } catch (SQLException e) {
            System.out.println("Failed to execute jobs_getJobInstances! " + e.getMessage());
        }
        return jobInstances;
    }
    public JobInstance jobs_getJobInstance(Player player, JobType jobType) {
        JobInstance jobInstance = null;
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM jobs WHERE job_id = ? AND player_id = ?")) {
            preparedStatement.setString(1, jobType.name);
            preparedStatement.setString(2, player.getUniqueId().toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                jobInstance = new JobInstance(resultSet.getInt("id"), JobType.get(resultSet.getString("job_id")), resultSet.getString("player_id"), resultSet.getInt("level"), resultSet.getInt("progression"));
            }
        } catch (SQLException e) {
            System.out.println("Failed to execute jobs_getJobInstance! " + e.getMessage());
        }
        return jobInstance;
    }



    public void environment_placeCustomBlock(Vector3i position, CustomBlockInstance.BLOCK_ID block_id, String world) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO environment_blocks (block_id, x, y, z, world) VALUES (?, ?, ?, ?, ?)")) {
            preparedStatement.setString(1, block_id.name());
            preparedStatement.setInt(2, position.x);
            preparedStatement.setInt(3, position.y);
            preparedStatement.setInt(4, position.z);
            preparedStatement.setString(5, world);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to execute environment_placeCustomBlock Vector3! " + e.getMessage());
        }
    }
    public void environment_placeCustomBlock(int x, int y, int z, CustomBlockInstance.BLOCK_ID block_id, String world) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO environment_blocks (block_id, x, y, z, world) VALUES (?, ?, ?, ?, ?)")) {
            preparedStatement.setString(1, block_id.name());
            preparedStatement.setInt(2, x);
            preparedStatement.setInt(3, y);
            preparedStatement.setInt(4, z);
            preparedStatement.setString(5, world);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to execute environment_placeCustomBlock! " + e.getMessage());
        }
    }
    public void environment_removeCustomBlock(int x, int y, int z, String world) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM environment_blocks WHERE x = ? AND y = ? AND z = ? AND world = ?")) {
            preparedStatement.setInt(1, x);
            preparedStatement.setInt(2, y);
            preparedStatement.setInt(3, z);
            preparedStatement.setString(4, world);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to execute environment_removeCustomBlock! " + e.getMessage());
        }
    }
    public void environment_removeCustomBlock(Vector3i position, String world) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM environment_blocks WHERE x = ? AND y = ? AND z = ? AND world = ?")) {
            preparedStatement.setInt(1, position.x);
            preparedStatement.setInt(2, position.y);
            preparedStatement.setInt(3, position.z);
            preparedStatement.setString(4, world);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to execute environment_removeCustomBlock Vector3! " + e.getMessage());
        }
    }

    public ArrayList<CustomBlockInstance> environment_getCustomBlockInstances(CustomBlockInstance.BLOCK_ID block_id, String world) {
        ArrayList<CustomBlockInstance> customBlockInstances = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT id, x, y, z FROM environment_blocks WHERE block_id = ? AND world = ?")) {
            preparedStatement.setString(1, block_id.name());
            preparedStatement.setString(2, world);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                customBlockInstances.add(new CustomBlockInstance(resultSet.getInt("id"), block_id, new Vector3i(resultSet.getInt("x"), resultSet.getInt("y"), resultSet.getInt("z")), world));
            }
        } catch (SQLException e) {
            System.out.println("Failed to execute environment_getCustomBlockInstances! " + e.getMessage());
        }
        return customBlockInstances;
    }
    public ArrayList<CustomBlockInstance> environment_getCustomBlockInstances() {
        ArrayList<CustomBlockInstance> customBlockInstances = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM environment_blocks")) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                customBlockInstances.add(new CustomBlockInstance(resultSet.getInt("id"), resultSet.getString("block_id"), new Vector3i(resultSet.getInt("x"), resultSet.getInt("y"), resultSet.getInt("z")), resultSet.getString("world")));
            }
        } catch (SQLException e) {
            System.out.println("Failed to execute environment_getCustomBlockInstances All! " + e.getMessage());
        }
        return customBlockInstances;
    }
    public CustomBlockInstance environment_getCustomBlockInstance(Vector3i position, String world) {
        CustomBlockInstance customBlockInstance = null;
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT id, block_id FROM environment_blocks WHERE x = ? AND y = ? AND z = ? AND world = ?")) {
            preparedStatement.setInt(1, position.x);
            preparedStatement.setInt(2, position.y);
            preparedStatement.setInt(3, position.z);
            preparedStatement.setString(4, world);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                customBlockInstance = new CustomBlockInstance(resultSet.getInt("id"), resultSet.getString("block_id"), position, world);
            }
        } catch (SQLException e) {
            System.out.println("Failed to execute environment_getCustomBlockInstance! " + e.getMessage());
        }
        return customBlockInstance;
    }
    public CustomBlockInstance environment_getCustomBlockInstance(int x, int y, int z, String world) {
        CustomBlockInstance customBlockInstance = null;
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT id, block_id FROM environment_blocks WHERE x = ? AND y = ? AND z = ? AND world = ?")) {
            preparedStatement.setInt(1, x);
            preparedStatement.setInt(2, y);
            preparedStatement.setInt(3, z);
            preparedStatement.setString(4, world);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                customBlockInstance = new CustomBlockInstance(resultSet.getInt("id"), resultSet.getString("block_id"), new Vector3i(x, y, z), world);
            }
        } catch (SQLException e) {
            System.out.println("Failed to execute environment_getCustomBlockInstance! " + e.getMessage());
        }
        return customBlockInstance;
    }



    public void health_addCondition(Player player, ConditionType conditionType) {
        String statement = "INSERT INTO health_conditions (player_id, condition_id";
        if (conditionType instanceof Illness) statement += ", time_before_next_stage) VALUES (?, ?, ?)";
        else statement += ") VALUES (?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(statement)) {
            preparedStatement.setString(1, player.getUniqueId().toString());
            preparedStatement.setString(2, conditionType.name);
            if (conditionType instanceof Illness){
                Illness illness = (Illness) conditionType;
                preparedStatement.setInt(3, illness.ticksToNextStage);
            }
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to execute health_addCondition! " + e.getMessage());
        }
    }
    public void health_removeCondition(Player player, ConditionType conditionType) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM health_conditions WHERE player_id = ? AND condition_id = ?")) {
            preparedStatement.setString(1, player.getUniqueId().toString());
            preparedStatement.setString(2, conditionType.name);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to execute health_removeCondition! " + e.getMessage());
        }
    }
    public void health_showCondition(Player player, ConditionType conditionType) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("UPDATE health_conditions SET hidden = FALSE WHERE player_id = ? AND condition_id = ?")) {
            preparedStatement.setString(1, player.getUniqueId().toString());
            preparedStatement.setString(2, conditionType.name);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to execute health_removeCondition! " + e.getMessage());
        }
    }
    public void health_addMedication(Player player, MedicationType medicationType) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "INSERT OR REPLACE INTO health_medications (player_id, medication_id, time_before_expiration, expired) " +
                        "VALUES (?, ?, ?, FALSE)")) {
            preparedStatement.setString(1, player.getUniqueId().toString());
            preparedStatement.setString(2, medicationType.name);
            preparedStatement.setInt(3, medicationType.timeBeforeExpiration);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to execute health_addMedication! " + e.getMessage());
        }
    }

    public void health_removeMedication(Player player, MedicationType medicationType) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM health_medications WHERE player_id = ? AND medication_id = ?")) {
            preparedStatement.setString(1, player.getUniqueId().toString());
            preparedStatement.setString(2, medicationType.name);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to execute health_removeMedication! " + e.getMessage());
        }
    }
    public void health_updateMedications(int ticks) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "UPDATE health_medications SET " +
                        "time_before_expiration = time_before_expiration - ?, " +
                        "expired = CASE " +
                            "WHEN time_before_expiration - ? <= 0 THEN TRUE " +
                            "ELSE FALSE " +
                        "END " +
                        "WHERE expired = FALSE AND player_id IN (SELECT player_id FROM players WHERE online = TRUE)")) {
            preparedStatement.setInt(1, ticks);
            preparedStatement.setInt(2, ticks);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to execute health_updateMedications! " + e.getMessage());
        }
    }
    public void health_updateConditions(int ticks) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "UPDATE health_conditions SET " +
                        "time_before_next_stage = time_before_next_stage - ? " +
                        "WHERE player_id IN (" +
                        "SELECT player_id FROM players WHERE online = TRUE)")){
            preparedStatement.setInt(1, ticks);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to execute health_updateConditions! " + e.getMessage());
        }
    }

    public void health_progressCondition(Player player, ConditionType conditionType) {
        String statement;
        if (conditionType instanceof Illness) statement = "UPDATE health_conditions SET stage = stage + 1, time_before_next_stage = ? WHERE player_id = ? AND condition_id = ?";
        else statement = "UPDATE health_conditions SET stage = stage + 1, time_before_next_stage = -1111 WHERE player_id = ? AND condition_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(statement)){

            if (conditionType instanceof Illness){
                Illness illness = (Illness) conditionType;
                preparedStatement.setInt(1, illness.ticksToNextStage);
                preparedStatement.setString(2, player.getUniqueId().toString());
                preparedStatement.setString(3, conditionType.name);
            }
            else {
                preparedStatement.setString(1, player.getUniqueId().toString());
                preparedStatement.setString(2, conditionType.name);
            }
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to execute health_progressCondition! " + e.getMessage());
        }
    }

    public void health_resetEverything(Player player) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM health_conditions WHERE player_id = ?")) {
            preparedStatement.setString(1, player.getUniqueId().toString());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to execute health_resetEverything Conditions! " + e.getMessage());
        }
        try (PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM health_medications WHERE player_id = ?")) {
            preparedStatement.setString(1, player.getUniqueId().toString());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to execute health_resetEverything Medications! " + e.getMessage());
        }
    }
    public void health_resetConditions(Player player) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM health_conditions WHERE player_id = ?")) {
            preparedStatement.setString(1, player.getUniqueId().toString());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to execute health_resetEverything Conditions! " + e.getMessage());
        }
    }
    public void health_resetMedications(Player player) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM health_medications WHERE player_id = ?")) {
            preparedStatement.setString(1, player.getUniqueId().toString());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to execute health_resetEverything Medications! " + e.getMessage());
        }
    }

    public ArrayList<ConditionInstance> health_getConditionInstances(Player player, boolean showHidden) {
        ArrayList<ConditionInstance> conditionInstances = new ArrayList<>();
        String statement = "SELECT * FROM health_conditions WHERE player_id = ?";
        if (!showHidden) statement += " AND hidden = FALSE";
        try (PreparedStatement preparedStatement = connection.prepareStatement(statement)) {
            preparedStatement.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                conditionInstances.add(new ConditionInstance(resultSet.getInt("id"), ConditionType.get(resultSet.getString("condition_id")), player.getUniqueId().toString(), resultSet.getInt("stage"), resultSet.getBoolean("hidden")));
            }
        } catch (SQLException e) {
            System.out.println("Failed to execute health_getConditionInstances! " + e.getMessage());
        }
        return conditionInstances;
    }
    public ArrayList<MedicationInstance> health_getMedicationInstances(Player player) {
        ArrayList<MedicationInstance> medicationInstances = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM health_medications WHERE player_id = ?")) {
            preparedStatement.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                medicationInstances.add(new MedicationInstance(resultSet.getInt("id"), MedicationType.get(resultSet.getString("medication_id")), player.getUniqueId().toString(), resultSet.getInt("time_before_expiration"), resultSet.getBoolean("expired")));
            }
        } catch (SQLException e) {
            System.out.println("Failed to execute health_getMedicationInstances! " + e.getMessage());
        }
        return medicationInstances;
    }
    public ArrayList<ConditionInstance> health_getLethalConditionInstances(Player player){
            ArrayList<ConditionInstance> conditionInstances = new ArrayList<>();
            String statement = "SELECT * FROM health_conditions WHERE player_id = ? AND stage > ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(statement)) {
                preparedStatement.setString(1, player.getUniqueId().toString());
                preparedStatement.setInt(2, ConditionStatus.maximalStage);
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()){
                    conditionInstances.add(new ConditionInstance(resultSet.getInt("id"), ConditionType.get(resultSet.getString("condition_id")), player.getUniqueId().toString(), resultSet.getInt("stage"), resultSet.getBoolean("hidden")));
                }
            } catch (SQLException e) {
                System.out.println("Failed to execute health_getLethalConditionInstances! " + e.getMessage());
            }
            return conditionInstances;
        }
    public ArrayList<ConditionInstance> health_getProgressedConditionInstances(Player player){
        ArrayList<ConditionInstance> conditionInstances = new ArrayList<>();
        String statement = "SELECT * FROM health_conditions WHERE player_id = ? AND time_before_next_stage < 0 AND time_before_next_stage > -1000";
        try (PreparedStatement preparedStatement = connection.prepareStatement(statement)) {
            preparedStatement.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                conditionInstances.add(new ConditionInstance(resultSet.getInt("id"), ConditionType.get(resultSet.getString("condition_id")), player.getUniqueId().toString(), resultSet.getInt("stage"), resultSet.getBoolean("hidden")));
            }
        } catch (SQLException e) {
            System.out.println("Failed to execute health_getProgressedConditionInstances! " + e.getMessage());
        }
        return conditionInstances;
    }
}




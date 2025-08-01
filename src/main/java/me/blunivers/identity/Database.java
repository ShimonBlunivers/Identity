package me.blunivers.identity;

import me.blunivers.identity.Environment.BlockInstance;
import me.blunivers.identity.Environment.BlockType;
import me.blunivers.identity.Health.Conditions.*;
import me.blunivers.identity.Health.Conditions.Illnesses.Illness;
import me.blunivers.identity.Jobs.JobType;
import me.blunivers.identity.Jobs.JobInstance;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.joml.Vector3i;

import java.sql.*;
import java.util.ArrayList;
import java.util.Map;

public class Database {

    private final Connection connection;

    public Database(String path) throws SQLException {

        connection = DriverManager.getConnection("jdbc:sqlite:" + path);
        try (Statement statement = connection.createStatement();) {
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
                        CREATE TABLE IF NOT EXISTS health_condition_types (
                        condition_id TEXT PRIMARY KEY,
                        time_before_next_stage INTEGER NOT NULL DEFAULT -88888,
                        illness BOOLEAN NOT NULL DEFAULT FALSE
                        );
                    """);
            // MEDICATION -> CONDITION TABLE
            statement.execute("""
                        CREATE TABLE IF NOT EXISTS health_medication_types (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        medication_id TEXT NOT NULL,
                        condition_id TEXT NOT NULL,
                        UNIQUE(medication_id, condition_id)
                        );
                    """);
            statement.execute("""
                        CREATE TABLE IF NOT EXISTS health_condition_instances (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        condition_id TEXT NOT NULL,
                        player_id TEXT NOT NULL,
                        stage INTEGER NOT NULL DEFAULT 1,
                        time_before_next_stage INTEGER NOT NULL DEFAULT -99999,
                        hidden BOOLEAN NOT NULL DEFAULT TRUE,
                        UNIQUE(player_id, condition_id)
                        );
                    """);
            statement.execute("""
                        CREATE TABLE IF NOT EXISTS health_medication_instances (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        medication_id TEXT NOT NULL,
                        player_id TEXT NOT NULL,
                        time_before_expiration INTEGER NOT NULL DEFAULT 72000,
                        expired BOOLEAN NOT NULL DEFAULT FALSE,
                        UNIQUE(player_id, medication_id)
                        );
                    """);

            initializeTables(statement);
        }
    }

    private void initializeTables(Statement statement) throws SQLException {
        for (ConditionType conditionType : ConditionType.conditions.values()) {
            if (conditionType instanceof Illness illness) {
                statement.execute(
                        "INSERT OR REPLACE INTO health_condition_types (condition_id, time_before_next_stage, illness) VALUES ('"
                                + illness.name + "', " + illness.ticksToNextStage + ", TRUE)");
            } else {
                statement.execute("INSERT OR REPLACE INTO health_condition_types (condition_id, illness) VALUES ("
                        + conditionType.name + ", FALSE)");
            }
        }
        for (MedicationType medicationType : MedicationType.medications.values()) {
            for (ConditionType against : medicationType.protectionAgainst) {
                statement.execute(
                        "INSERT OR REPLACE INTO health_medication_types (medication_id, condition_id) VALUES ('"
                                + medicationType.name + "', '" + against.name + "')");
            }
        }
    }

    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    public void players_join(Player player) {
        try (PreparedStatement preparedStatement = connection
                .prepareStatement("INSERT OR REPLACE INTO players (player_id, online) VALUES (?, TRUE)")) {
            preparedStatement.setString(1, player.getUniqueId().toString());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to execute players_join! " + e.getMessage());
        }
    }

    public void players_leave(Player player) {
        try (PreparedStatement preparedStatement = connection
                .prepareStatement("UPDATE players SET ONLINE = FALSE WHERE player_id = ?")) {
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
        try (PreparedStatement preparedStatement = connection
                .prepareStatement("INSERT INTO jobs (job_id, player_id) VALUES (?, ?)")) {
            preparedStatement.setString(1, jobType.name);
            preparedStatement.setString(2, player.getUniqueId().toString());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to execute jobs_employPlayer! " + e.getMessage());
        }
    }

    public void jobs_leaveJob(Player player, JobType jobType) {
        try (PreparedStatement preparedStatement = connection
                .prepareStatement("DELETE FROM jobs WHERE job_id = ? AND player_id = ?")) {
            preparedStatement.setString(1, jobType.name);
            preparedStatement.setString(2, player.getUniqueId().toString());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to execute jobs_leaveJob! " + e.getMessage());
        }
    }

    public void jobs_updateProgress(Player player, JobType jobType, int level, int progression) {
        try (PreparedStatement preparedStatement = connection
                .prepareStatement("UPDATE jobs SET level = ?, progression = ? WHERE job_id = ? AND player_id = ?")) {
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
        try (PreparedStatement preparedStatement = connection
                .prepareStatement("SELECT job_id FROM jobs WHERE player_id = ?")) {
            preparedStatement.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                jobTypes.add(JobType.get(resultSet.getString("job_id")));
            }
        } catch (SQLException e) {
            System.out.println("Failed to execute jobs_getJobTypes! " + e.getMessage());
        }
        return jobTypes;
    }

    public ArrayList<JobInstance> jobs_getJobInstances(Player player) {
        ArrayList<JobInstance> jobInstances = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection
                .prepareStatement("SELECT * FROM jobs WHERE player_id = ?")) {
            preparedStatement.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                jobInstances.add(new JobInstance(resultSet.getInt("id"), JobType.get(resultSet.getString("job_id")),
                        resultSet.getString("player_id"), resultSet.getInt("level"), resultSet.getInt("progression")));
            }
        } catch (SQLException e) {
            System.out.println("Failed to execute jobs_getJobInstances! " + e.getMessage());
        }
        return jobInstances;
    }

    public JobInstance jobs_getJobInstance(Player player, JobType jobType) {
        JobInstance jobInstance = null;
        try (PreparedStatement preparedStatement = connection
                .prepareStatement("SELECT * FROM jobs WHERE job_id = ? AND player_id = ?")) {
            preparedStatement.setString(1, jobType.name);
            preparedStatement.setString(2, player.getUniqueId().toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                jobInstance = new JobInstance(resultSet.getInt("id"), JobType.get(resultSet.getString("job_id")),
                        resultSet.getString("player_id"), resultSet.getInt("level"), resultSet.getInt("progression"));
            }
        } catch (SQLException e) {
            System.out.println("Failed to execute jobs_getJobInstance! " + e.getMessage());
        }
        return jobInstance;
    }

    public BlockInstance environment_placeCustomBlock(int x, int y, int z, BlockType blockType, String world,
            String metadata) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "INSERT INTO environment_blocks (block_id, x, y, z, world, metadata) VALUES (?, ?, ?, ?, ?, ?)")) {
            preparedStatement.setString(1, blockType.name);
            preparedStatement.setInt(2, x);
            preparedStatement.setInt(3, y);
            preparedStatement.setInt(4, z);
            preparedStatement.setString(5, world);
            preparedStatement.setString(6, metadata);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to execute environment_placeCustomBlock with metadata! " + e.getMessage());
        }
        return environment_getCustomBlockInstance(x, y, z, world);
    }

    public BlockInstance environment_placeCustomBlock(int x, int y, int z, BlockType blockType, String world) {
        try (PreparedStatement preparedStatement = connection
                .prepareStatement("INSERT INTO environment_blocks (block_id, x, y, z, world) VALUES (?, ?, ?, ?, ?)")) {
            preparedStatement.setString(1, blockType.name);
            preparedStatement.setInt(2, x);
            preparedStatement.setInt(3, y);
            preparedStatement.setInt(4, z);
            preparedStatement.setString(5, world);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to execute environment_placeCustomBlock! " + e.getMessage());
        }
        return environment_getCustomBlockInstance(x, y, z, world);
    }

    public boolean environment_removeMetadataFromBlock(int x, int y, int z, String world, String metadataKey) {
        String currentMetadata = null;
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT metadata FROM environment_blocks WHERE x = ? AND y = ? AND z = ? AND world = ?")) {
            preparedStatement.setInt(1, x);
            preparedStatement.setInt(2, y);
            preparedStatement.setInt(3, z);
            preparedStatement.setString(4, world);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                currentMetadata = resultSet.getString("metadata");
            } 
        } catch (SQLException e) {
            System.out.println("Failed to execute environment_removeMetadataFromBlock! " + e.getMessage());
        }
        if (currentMetadata == null || currentMetadata.isEmpty()) {
            return false;
        }
        if (!currentMetadata.contains(metadataKey)) {
            return false;
        }

        Map<String, String> metadataMap = Utility.metadataToMap(currentMetadata);
        metadataMap.remove(metadataKey);

        currentMetadata = Utility.metadataSerialize(metadataMap);

        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "UPDATE environment_blocks SET metadata = ? WHERE x = ? AND y = ? AND z = ? AND world = ?")) {
            preparedStatement.setString(1, currentMetadata);
            preparedStatement.setInt(2, x);
            preparedStatement.setInt(3, y);
            preparedStatement.setInt(4, z);
            preparedStatement.setString(5, world);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to execute environment_removeMetadataFromBlock! " + e.getMessage());
        }
        return true;
    }

    public void environment_addMetadataToBlock(int x, int y, int z, String world, String key, String metadata) {
        String currentMetadata = "";
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT metadata FROM environment_blocks WHERE x = ? AND y = ? AND z = ? AND world = ?")) {
            preparedStatement.setInt(1, x);
            preparedStatement.setInt(2, y);
            preparedStatement.setInt(3, z);
            preparedStatement.setString(4, world);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                currentMetadata = resultSet.getString("metadata");
            } else {
                return;
            }
        } catch (SQLException e) {
            System.out.println("Failed to execute environment_addMetadataToBlock! " + e.getMessage());
        }
        if (currentMetadata == null || currentMetadata.isEmpty()) {
            currentMetadata = key + ":" + metadata;
        } else {
            Map<String, String> metadataMap = Utility.metadataToMap(currentMetadata);
            metadataMap.put(key, metadata);
            currentMetadata = Utility.metadataSerialize(metadataMap);
        }
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "UPDATE environment_blocks SET metadata = ? WHERE x = ? AND y = ? AND z = ? AND world = ?")) {
            preparedStatement.setString(1, currentMetadata);
            System.out.println(x);
            System.out.println(y);
            System.out.println(z);
            System.out.println(world);
            System.out.println(currentMetadata);
            preparedStatement.setInt(2, x);
            preparedStatement.setInt(3, y);
            preparedStatement.setInt(4, z);
            preparedStatement.setString(5, world);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to execute environment_addMetadataToBlock! " + e.getMessage());
        }
    }

    public void environment_removeCustomBlock(int x, int y, int z, String world) {
        try (PreparedStatement preparedStatement = connection
                .prepareStatement("DELETE FROM environment_blocks WHERE x = ? AND y = ? AND z = ? AND world = ?")) {
            preparedStatement.setInt(1, x);
            preparedStatement.setInt(2, y);
            preparedStatement.setInt(3, z);
            preparedStatement.setString(4, world);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to execute environment_removeCustomBlock! " + e.getMessage());
        }
    }

    public ArrayList<BlockInstance> environment_getCustomBlockInstances(BlockType blockType, String world) {
        ArrayList<BlockInstance> blockInstances = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT id, x, y, z, metadata FROM environment_blocks WHERE block_id = ? AND world = ?")) {
            preparedStatement.setString(1, blockType.name);
            preparedStatement.setString(2, world);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                blockInstances.add(new BlockInstance(resultSet.getInt("id"), blockType,
                        new Vector3i(resultSet.getInt("x"), resultSet.getInt("y"), resultSet.getInt("z")),
                        Bukkit.getWorld(world), resultSet.getString("metadata")));
            }
        } catch (SQLException e) {
            System.out.println("Failed to execute environment_getCustomBlockInstances! " + e.getMessage());
        }
        return blockInstances;
    }

    public ArrayList<BlockInstance> environment_getCustomBlockInstances() {
        ArrayList<BlockInstance> blockInstances = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM environment_blocks")) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                blockInstances
                        .add(new BlockInstance(resultSet.getInt("id"), BlockType.get(resultSet.getString("block_id")),
                                new Vector3i(resultSet.getInt("x"), resultSet.getInt("y"), resultSet.getInt("z")),
                                Bukkit.getWorld(resultSet.getString("world")), resultSet.getString("metadata")));
            }
        } catch (SQLException e) {
            System.out.println("Failed to execute environment_getCustomBlockInstances All! " + e.getMessage());
        }
        return blockInstances;
    }

    public BlockInstance environment_getCustomBlockInstance(int x, int y, int z, String world) {
        BlockInstance blockInstance = null;
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT id, block_id, metadata FROM environment_blocks WHERE x = ? AND y = ? AND z = ? AND world = ?")) {
            preparedStatement.setInt(1, x);
            preparedStatement.setInt(2, y);
            preparedStatement.setInt(3, z);
            preparedStatement.setString(4, world);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                blockInstance = new BlockInstance(resultSet.getInt("id"),
                        BlockType.get(resultSet.getString("block_id")), new Vector3i(x, y, z), Bukkit.getWorld(world),
                        resultSet.getString("metadata"));
            }
        } catch (SQLException e) {
            System.out.println("Failed to execute environment_getCustomBlockInstance! " + e.getMessage());
        }
        return blockInstance;
    }

    public void health_addCondition(Player player, ConditionType conditionType) {
        String statement = "INSERT INTO health_condition_instances (player_id, condition_id";
        if (conditionType instanceof Illness)
            statement += ", time_before_next_stage, time_before_next_stage) VALUES (?, ?, ?, ?)";
        else
            statement += ") VALUES (?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(statement)) {
            preparedStatement.setString(1, player.getUniqueId().toString());
            preparedStatement.setString(2, conditionType.name);
            if (conditionType instanceof Illness illness) {
                preparedStatement.setInt(3, illness.ticksToNextStage);
                preparedStatement.setInt(4, illness.ticksToNextStage);
            }
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to execute health_addCondition! " + e.getMessage());
        }
    }

    public void health_removeCondition(Player player, ConditionType conditionType) {
        try (PreparedStatement preparedStatement = connection
                .prepareStatement("DELETE FROM health_condition_instances WHERE player_id = ? AND condition_id = ?")) {
            preparedStatement.setString(1, player.getUniqueId().toString());
            preparedStatement.setString(2, conditionType.name);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to execute health_removeCondition! " + e.getMessage());
        }
    }

    public void health_showCondition(Player player, ConditionType conditionType) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "UPDATE health_condition_instances SET hidden = FALSE WHERE player_id = ? AND condition_id = ?")) {
            preparedStatement.setString(1, player.getUniqueId().toString());
            preparedStatement.setString(2, conditionType.name);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to execute health_removeCondition! " + e.getMessage());
        }
    }

    public void health_addMedication(Player player, MedicationType medicationType) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "INSERT OR REPLACE INTO health_medication_instances (player_id, medication_id, time_before_expiration, expired) "
                        +
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
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "DELETE FROM health_medication_instances WHERE player_id = ? AND medication_id = ?")) {
            preparedStatement.setString(1, player.getUniqueId().toString());
            preparedStatement.setString(2, medicationType.name);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to execute health_removeMedication! " + e.getMessage());
        }
    }

    public void health_updateMedications(int ticks) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "UPDATE health_medication_instances SET " +
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
                "UPDATE health_condition_instances SET time_before_next_stage = time_before_next_stage - ? " +
                        "WHERE player_id IN (SELECT player_id FROM players WHERE online = TRUE)")) {
            preparedStatement.setInt(1, ticks);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to execute health_updateConditions! " + e.getMessage());
        }
    }

    public void health_progressConditions() {
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "UPDATE health_condition_instances " +
                        "SET stage = CASE " +
                        "WHEN EXISTS (" +
                        "SELECT 1 FROM health_medication_types hmt " +
                        "JOIN health_medication_instances hmi ON hmt.medication_id = hmi.medication_id " +
                        "WHERE hmt.condition_id = health_condition_instances.condition_id " +
                        "AND hmi.player_id = health_condition_instances.player_id AND hmi.expired = FALSE) " +
                        "THEN stage - 1 " +
                        "ELSE stage + 1 " +
                        "END, " +
                        "time_before_next_stage = (" +
                        "SELECT time_before_next_stage FROM health_condition_types " +
                        "WHERE condition_id = health_condition_instances.condition_id) " +
                        "WHERE time_before_next_stage <= 0 AND time_before_next_stage > -77777")) {
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to execute health_progressConditions! " + e.getMessage());
        }
    }

    public void health_healConditions() {
        // ArrayList<ConditionInstance> conditionInstances = new ArrayList<>();
        String statement = "DELETE FROM health_condition_instances WHERE stage <= 0";
        try (PreparedStatement preparedStatement = connection.prepareStatement(statement)) {
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to execute health_healConditions! " + e.getMessage());
        }
    }

    public void health_resetEverything(Player player) {
        try (PreparedStatement preparedStatement = connection
                .prepareStatement("DELETE FROM health_condition_instances WHERE player_id = ?")) {
            preparedStatement.setString(1, player.getUniqueId().toString());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to execute health_resetEverything Conditions! " + e.getMessage());
        }
        try (PreparedStatement preparedStatement = connection
                .prepareStatement("DELETE FROM health_medication_instances WHERE player_id = ?")) {
            preparedStatement.setString(1, player.getUniqueId().toString());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to execute health_resetEverything Medications! " + e.getMessage());
        }
    }

    public void health_resetConditions(Player player) {
        try (PreparedStatement preparedStatement = connection
                .prepareStatement("DELETE FROM health_condition_instances WHERE player_id = ?")) {
            preparedStatement.setString(1, player.getUniqueId().toString());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to execute health_resetEverything Conditions! " + e.getMessage());
        }
    }

    public void health_resetMedications(Player player) {
        try (PreparedStatement preparedStatement = connection
                .prepareStatement("DELETE FROM health_medication_instances WHERE player_id = ?")) {
            preparedStatement.setString(1, player.getUniqueId().toString());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to execute health_resetEverything Medications! " + e.getMessage());
        }
    }

    public ArrayList<ConditionInstance> health_getConditionInstances(Player player, boolean showHidden) {
        ArrayList<ConditionInstance> conditionInstances = new ArrayList<>();
        String statement = "SELECT * FROM health_condition_instances WHERE player_id = ?";
        if (!showHidden)
            statement += " AND hidden = FALSE";
        try (PreparedStatement preparedStatement = connection.prepareStatement(statement)) {
            preparedStatement.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                conditionInstances.add(new ConditionInstance(resultSet.getInt("id"),
                        ConditionType.get(resultSet.getString("condition_id")), player.getUniqueId().toString(),
                        resultSet.getInt("stage"), resultSet.getBoolean("hidden")));
            }
        } catch (SQLException e) {
            System.out.println("Failed to execute health_getConditionInstances! " + e.getMessage());
        }
        return conditionInstances;
    }

    public ArrayList<MedicationInstance> health_getMedicationInstances(Player player) {
        ArrayList<MedicationInstance> medicationInstances = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection
                .prepareStatement("SELECT * FROM health_medication_instances WHERE player_id = ?")) {
            preparedStatement.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                medicationInstances.add(new MedicationInstance(resultSet.getInt("id"),
                        MedicationType.get(resultSet.getString("medication_id")), player.getUniqueId().toString(),
                        resultSet.getInt("time_before_expiration"), resultSet.getBoolean("expired")));
            }
        } catch (SQLException e) {
            System.out.println("Failed to execute health_getMedicationInstances! " + e.getMessage());
        }
        return medicationInstances;
    }

    public ArrayList<ConditionInstance> health_getLethalConditionInstances() {
        ArrayList<ConditionInstance> conditionInstances = new ArrayList<>();
        String statement = "SELECT * FROM health_condition_instances WHERE stage > ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(statement)) {
            preparedStatement.setInt(1, ConditionStatus.maximalStage);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                conditionInstances.add(new ConditionInstance(resultSet.getInt("id"),
                        ConditionType.get(resultSet.getString("condition_id")), resultSet.getString("player_id"),
                        resultSet.getInt("stage"), resultSet.getBoolean("hidden")));
            }
        } catch (SQLException e) {
            System.out.println("Failed to execute health_getLethalConditionInstances! " + e.getMessage());
        }
        return conditionInstances;
    }

    public ArrayList<ConditionInstance> health_getProgressedConditionInstances(Player player) {
        ArrayList<ConditionInstance> conditionInstances = new ArrayList<>();
        String statement = "SELECT * FROM health_condition_instances WHERE player_id = ? AND time_before_next_stage < 0 AND time_before_next_stage > -1000";
        try (PreparedStatement preparedStatement = connection.prepareStatement(statement)) {
            preparedStatement.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                conditionInstances.add(new ConditionInstance(resultSet.getInt("id"),
                        ConditionType.get(resultSet.getString("condition_id")), player.getUniqueId().toString(),
                        resultSet.getInt("stage"), resultSet.getBoolean("hidden")));
            }
        } catch (SQLException e) {
            System.out.println("Failed to execute health_getProgressedConditionInstances! " + e.getMessage());
        }
        return conditionInstances;
    }
}
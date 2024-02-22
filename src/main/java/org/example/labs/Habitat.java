package org.example.labs;

import java.util.List;

import javafx.animation.AnimationTimer;
import javafx.scene.layout.StackPane;
import javafx.scene.Node;
import java.util.ArrayList; // Добавим импорт для ArrayList
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

import java.util.Random;

class Habitat {
    private StackPane root;
    private Random random;
    private AnimationTimer simulationTimer;
    private List<Ant> ants; // Добавим объявление переменной ants
    private long simulationStartTime; // Время начала симуляции
    private Label statisticsLabel; // Label для вывода статистики

    private static final int N1 = 5; // Интервал для рабочих муравьев (в секундах)
    private static final double P1 = 0.3; // Вероятность для рабочих муравьев
    private static final int N2 = 3; // интервал для муравьев-воинов (в секундах)
    private static final double P2 = 0.5; // вероятность для муравьев-воинов

    public Habitat(StackPane root) {
        this.root = root;
        this.random = new Random();
        this.simulationTimer = createSimulationTimer();
        this.ants = new ArrayList<>(); // Инициализируем переменную ants
        this.statisticsLabel = new Label();
    }

    public void startSimulation() {
        simulationStartTime = System.currentTimeMillis();
        ants.clear(); // Очищаем список муравьев
        simulationTimer.start();
    }

    public void stopSimulation() {
        simulationTimer.stop();
        root.getChildren().clear();
        updateStatistics();
    }

    private AnimationTimer createSimulationTimer() {
        long[] lastSpawnTimeWorker = { System.nanoTime() };
        long[] lastSpawnTimeWarrior = { System.nanoTime() };

        return new AnimationTimer() {
            @Override
            public void handle(long now) {
                long elapsedTimeWorker = (now - lastSpawnTimeWorker[0]) / 1_000_000_000;
                long elapsedTimeWarrior = (now - lastSpawnTimeWarrior[0]) / 1_000_000_000;

                if (elapsedTimeWorker >= N1 && random.nextDouble() <= P1) {
                    spawnAnt(new WorkerAnt());
                    lastSpawnTimeWorker[0] = now;
                }

                if (elapsedTimeWarrior >= N2 && random.nextDouble() <= P2) {
                    spawnAnt(new WarriorAnt());
                    lastSpawnTimeWarrior[0] = now;
                }
            }
        };
    }

    private void spawnAnt(Ant ant) {
        ant.getImageView().setTranslateX(random.nextDouble() * 600);
        ant.getImageView().setTranslateY(random.nextDouble() * 400);
        root.getChildren().add(ant.getImageView());
    }

    private void updateStatistics() {
        long simulationEndTime = System.currentTimeMillis();
        long simulationTime = (simulationEndTime - simulationStartTime) / 1000;

        int workerAntsCount = 0;
        int warriorAntsCount = 0;

        for (Ant ant : ants) {
            if (ant instanceof WorkerAnt) {
                workerAntsCount++;
            } else if (ant instanceof WarriorAnt) {
                warriorAntsCount++;
            }
        }

        // Очищаем root от всех элементов, включая статистику
        root.getChildren().clear();

        String statistics = String.format("Simulation Time: %d seconds\nWorker Ants: %d\nWarrior Ants: %d",
                simulationTime, workerAntsCount, warriorAntsCount);

        // Выводим статистику
        statisticsLabel.setText(statistics);
        root.getChildren().add(statisticsLabel);
    }

    // Метод для поиска муравья по его ImageView
    private Ant findAntByImageView(ImageView imageView) {
        for (Ant ant : ants) {
            if (ant.getImageView() == imageView) {
                return ant;
            }
        }
        return null;
    }
}
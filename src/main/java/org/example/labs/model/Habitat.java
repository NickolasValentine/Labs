package org.example.labs.model;

import java.util.List;

import javafx.animation.AnimationTimer;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import java.util.ArrayList; // Добавим импорт для ArrayList
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;

import java.util.Optional;
import java.util.Random;

public class Habitat {
    private StackPane root;
    private StackPane AntList;
    private Random random;
    private AnimationTimer simulationTimer;
    private List<Ant> ants; // Добавим объявление переменной ants
    private long simulationStartTime; // Время начала симуляции
    private Label statisticsLabel; // Label для вывода статистики
    private Rectangle statisticsRectangle; // Rectangle для вывода статистики
    public boolean moreInfo;
    private boolean startFlag; // Флаг для проверки работы симуляции
    public int N1; // Интервал для рабочих муравьев (в секундах)
    public double P1; // Вероятность для рабочих муравьев
    public int N2; // интервал для муравьев-воинов (в секундах)
    public double P2; // вероятность для муравьев-воинов

    public Habitat(StackPane root, StackPane AntList) {
        this.root = root;
        this.AntList = AntList;
        this.random = new Random();
        this.simulationTimer = createSimulationTimer();
        this.ants = new ArrayList<>(); // Инициализируем переменную ants
        this.statisticsLabel = new Label();
        this.statisticsRectangle = new Rectangle();
    }

    public void startSimulation() {
        simulationStartTime = System.currentTimeMillis();
        AntList.getChildren().clear();
        ants.clear(); // Очищаем список муравьев
        statisticsLabel.setText("");
        root.getChildren().remove(statisticsRectangle);
        root.getChildren().remove(statisticsLabel);
        simulationTimer.start();
        System.out.println(P1 + "   " + P2);
    }

    public void stopSimulation() {
        simulationTimer.stop();
        //root.getChildren().clear();
        AntList.getChildren().clear();
        if(moreInfo) updateStatistics();
    }

    private AnimationTimer createSimulationTimer() {
        long startTime = System.nanoTime();
        final long[] lastWorkerTime = {0};
        final long[] lastWarriorTime = {0};

        return new AnimationTimer() {
            @Override
            public void handle(long now) {
                long elapsedTime = (now - startTime) / 1_000_000_000;

                // Проверяем, прошло ли достаточно времени с момента последнего выполнения условия для рабочего муравья
                if (elapsedTime - lastWorkerTime[0] >= N1) {
                    double pp1 = random.nextDouble();
                    if (pp1 <= P1) {
                        spawnAnt(new WorkerAnt());
                    }
                    lastWorkerTime[0] = elapsedTime;
                }

                // Проверяем, прошло ли достаточно времени с момента последнего выполнения условия для воинственного муравья
                if (elapsedTime - lastWarriorTime[0] >= N2) {
                    if (random.nextDouble() <= P2) {
                        spawnAnt(new WarriorAnt());
                    }
                    lastWarriorTime[0] = elapsedTime;
                }
            }
        };
    }

    private void spawnAnt(Ant ant) {
        // Отменяем центрирование только для добавленных муравьев
        StackPane.setAlignment(ant.getImageView(), Pos.TOP_LEFT);
        ant.getImageView().setTranslateX(random.nextDouble() * 1150);
        ant.getImageView().setTranslateY(random.nextDouble() * 850);
        //root.getChildren().add(ant.getImageView());
        AntList.getChildren().add(ant.getImageView());
        ants.add(ant);
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

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Статистика");
        alert.setHeaderText("OK - прекратить симуляцию\nОтмена - продолжить симуляцию");

        String simulationTimeString;
        long hours = simulationTime / 3600;
        long minutes = (simulationTime % 3600) / 60;
        long seconds = simulationTime % 60;
        simulationTimeString = String.format("Время: %02d:%02d:%02d\n", hours, minutes, seconds);

        String statistics = String.format("%sРабочих: %d\nСолдат Ants: %d",
                simulationTimeString, workerAntsCount, warriorAntsCount);

        TextArea textArea = new TextArea(statistics);
        textArea.setPrefColumnCount(20);
        textArea.setPrefRowCount(5);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        alert.getDialogPane().setContent(textArea);
        alert.showAndWait();

    }
}
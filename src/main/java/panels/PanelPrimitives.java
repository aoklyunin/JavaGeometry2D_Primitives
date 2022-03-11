package panels;

import app.Line;
import app.Primitive;
import io.github.humbleui.jwm.Event;
import io.github.humbleui.jwm.EventKey;
import io.github.humbleui.jwm.Window;
import io.github.humbleui.skija.Canvas;
import io.github.humbleui.skija.Paint;
import io.github.humbleui.skija.RRect;
import io.github.humbleui.skija.Rect;
import misc.CoordinateSystem2i;
import misc.Misc;
import misc.Vector2d;
import misc.Vector2i;

import java.util.ArrayList;

/**
 * Панель игры
 */
public class PanelPrimitives extends Panel {
    /**
     * Список примитивов
     */
    private final ArrayList<Primitive> primitives = new ArrayList<>();
    /**
     * Положение текущего примитива
     */
    private int primitivePos;

    /**
     * Конструктор панели
     *
     * @param window          окно
     * @param drawBG          нужно ли рисовать подложку
     * @param backgroundColor цвет фона
     * @param padding         отступы
     */
    public PanelPrimitives(Window window, boolean drawBG, int backgroundColor, int padding) {
        super(window, drawBG, backgroundColor, padding);
        // добавляем точку
        primitives.add(((canvas, windowCS, p) -> canvas.drawRRect(
                RRect.makeXYWH(200, 200, 4, 4, 2), p)
        ));
        // добавляем линию
        primitives.add((canvas, windowCS, p) -> {
            // опорные точки линии
            Vector2i pointA = new Vector2i(200, 200);
            Vector2i pointB = new Vector2i(500, 600);
            // вектор, ведущий из точки A в точку B
            Vector2i delta = Vector2i.subtract(pointA, pointB);
            // получаем максимальную длину отрезка на экране, как длину диагонали экрана
            int maxDistance = (int) windowCS.getSize().length();
            // получаем новые точки для рисования, которые гарантируют, что линия
            // будет нарисована до границ экрана
            Vector2i renderPointA = Vector2i.sum(pointA, Vector2i.mult(delta, maxDistance));
            Vector2i renderPointB = Vector2i.sum(pointA, Vector2i.mult(delta, -maxDistance));
            // рисуем линию
            canvas.drawLine(renderPointA.x, renderPointA.y, renderPointB.x, renderPointB.y, p);
        });
        // добавляем треугольник
        primitives.add((canvas, windowCS, p) -> {
            // вершины треугольника
            Vector2i pointA = new Vector2i(400, 200);
            Vector2i pointB = new Vector2i(500, 600);
            Vector2i pointC = new Vector2i(300, 400);
            // рисуем его стороны
            canvas.drawLine(pointA.x, pointA.y, pointB.x, pointB.y, p);
            canvas.drawLine(pointB.x, pointB.y, pointC.x, pointC.y, p);
            canvas.drawLine(pointC.x, pointC.y, pointA.x, pointA.y, p);
        });
        // добавляем окружность
        primitives.add((canvas, windowCS, p) -> {
            // центр окружности
            Vector2i center = new Vector2i(350, 350);
            // радиус окружности
            int rad = 200;
            // радиус вдоль оси x
            int radX = (int) (rad * 1.3);
            // радиус вдоль оси y
            int radY = (int) (rad * 0.9);
            // кол-во отсчётов цикла
            int loopCnt = 40;
            // создаём массив координат опорных точек
            float[] points = new float[loopCnt * 4];
            // запускаем цикл
            for (int i = 0; i < loopCnt; i++) {
                // x координата первой точки
                points[i * 4] = (float) (center.x + radX * Math.cos(Math.PI / 20 * i));
                // y координата первой точки
                points[i * 4 + 1] = (float) (center.x + radY * Math.sin(Math.PI / 20 * i));

                // x координата второй точки
                points[i * 4 + 2] = (float) (center.x + radX * Math.cos(Math.PI / 20 * (i + 1)));
                // y координата второй точки
                points[i * 4 + 3] = (float) (center.x + radY * Math.sin(Math.PI / 20 * (i + 1)));
            }
            // рисуем линии
            canvas.drawLines(points, p);
        });
        // добавляем параллельный прямоугольник
        primitives.add((canvas, windowCS, p) -> {
            // левая верхняя вершина
            Vector2i pointA = new Vector2i(200, 100);
            // правая нижняя
            Vector2i pointC = new Vector2i(300, 500);

            // рассчитываем опорные точки прямоугольника
            Vector2i pointB = new Vector2i(pointA.x, pointC.y);
            Vector2i pointD = new Vector2i(pointC.x, pointA.y);

            // рисуем его стороны
            canvas.drawLine(pointA.x, pointA.y, pointB.x, pointB.y, p);
            canvas.drawLine(pointB.x, pointB.y, pointC.x, pointC.y, p);
            canvas.drawLine(pointC.x, pointC.y, pointD.x, pointD.y, p);
            canvas.drawLine(pointD.x, pointD.y, pointA.x, pointA.y, p);

        });
        // добавляем прямоугольник
        primitives.add((canvas, windowCS, p) -> {
            // первая вершина
            Vector2i pointA = new Vector2i(200, 100);
            // вторая вершина
            Vector2i pointB = new Vector2i(400, 300);
            // точка на противоположной стороне
            Vector2i pointP = new Vector2i(100, 300);
            // создаём линию
            Line line = new Line(new Vector2d(pointA), new Vector2d(pointB));
            // рассчитываем расстояние от прямой до точки
            double dist = line.getDistance(new Vector2d(pointP));
            // рассчитываем векторы для векторного умножения
            Vector2d AB = Vector2d.subtract(new Vector2d(pointB), new Vector2d(pointA));
            Vector2d AP = Vector2d.subtract(new Vector2d(pointP), new Vector2d(pointA));
            // определяем направление смещения
            double direction = Math.signum(AB.cross(AP));
            // получаем вектор смещения
            Vector2i offset = AB.rotated(Math.PI / 2 * direction).norm().mult(dist).intVector();

            // находим координаты вторых двух вершин прямоугольника
            Vector2i pointC = Vector2i.sum(pointB, offset);
            Vector2i pointD = Vector2i.sum(pointA, offset);

            // рисуем его стороны
            canvas.drawLine(pointA.x, pointA.y, pointB.x, pointB.y, p);
            canvas.drawLine(pointB.x, pointB.y, pointC.x, pointC.y, p);
            canvas.drawLine(pointC.x, pointC.y, pointD.x, pointD.y, p);
            canvas.drawLine(pointD.x, pointD.y, pointA.x, pointA.y, p);

            // сохраняем цвет рисования
            int paintColor = p.getColor();
            // задаём красный цвет
            p.setColor(Misc.getColor(200, 255, 0, 0));
            // рисуем опорные точки
            canvas.drawRRect(RRect.makeXYWH(pointA.x - 4, pointA.y - 4, 8, 8, 4), p);
            canvas.drawRRect(RRect.makeXYWH(pointB.x - 4, pointB.y - 4, 8, 8, 4), p);
            canvas.drawRRect(RRect.makeXYWH(pointP.x - 4, pointP.y - 4, 8, 8, 4), p);
            // восстанавливаем исходный цвет рисования
            p.setColor(paintColor);
        });


        primitivePos = primitives.size() - 1;
    }

    /**
     * Обработчик событий
     *
     * @param e событие
     */
    @Override
    public void accept(Event e) {
        // кнопки клавиатуры
        if (e instanceof EventKey eventKey) {
            // кнопка нажата с Ctrl
            if (eventKey.isPressed()) {
                switch (eventKey.getKey()) {
                    // Следующий примитив
                    case LEFT -> primitivePos = (primitivePos - 1 + primitives.size()) % primitives.size();
                    // Предыдущий примитив
                    case RIGHT -> primitivePos = (primitivePos + 1) % primitives.size();
                }
            }
        }
    }


    /**
     * Метод под рисование в конкретной реализации
     *
     * @param canvas   область рисования
     * @param windowCS СК окна
     */
    @Override
    public void paintImpl(Canvas canvas, CoordinateSystem2i windowCS) {
        // создаём перо
        Paint p = new Paint();
        // задаём цвет
        p.setColor(Misc.getColor(200, 255, 255, 255));
        // задаём толщину пера
        p.setStrokeWidth(5);
        // рисуем текущий примитив
        primitives.get(primitivePos).render(canvas, windowCS, p);
    }

}

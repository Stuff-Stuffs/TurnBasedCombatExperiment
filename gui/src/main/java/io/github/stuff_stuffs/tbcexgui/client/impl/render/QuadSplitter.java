package io.github.stuff_stuffs.tbcexgui.client.impl.render;

import io.github.stuff_stuffs.tbcexgui.client.api.GuiQuad;
import io.github.stuff_stuffs.tbcexgui.client.api.GuiQuadEmitter;
import io.github.stuff_stuffs.tbcexutil.common.Vec2d;

import java.util.ArrayList;
import java.util.List;

public final class QuadSplitter {
    private QuadSplitter() {
    }

    public static void scissor(final GuiQuad quad, final double x0, final double y0, final double x1, final double y1, final GuiQuadEmitter emitter) {
        final List<Vec2d> poly = quadClip(quad, x0, y0, x1, y1);
        if (poly.size() < 3) {
            return;
        }
        final Vec2d constVertex = poly.remove(poly.size() - 1);
        final InterpResult result = new InterpResult(new Vec2d(quad.x(0), quad.y(0)), new Vec2d(quad.x(2), quad.y(2)));
        final int size = poly.size();
        for (int i = 0; i < size; i++) {
            final Vec2d firstVertex = poly.get(i);
            final Vec2d secondVertex = poly.get((i + 1) % size);
            emitter.renderMaterial(quad.renderMaterial());
            emitter.depth(quad.depth());
            interpolate(constVertex, quad, result);
            emitter.interpolate(0, quad, result.v0, result.v1, result.v2, result.v3);
            emitter.pos(0, (float) constVertex.x, (float) constVertex.y);
            interpolate(firstVertex, quad, result);
            emitter.interpolate(1, quad, result.v0, result.v1, result.v2, result.v3);
            emitter.pos(1, (float) constVertex.x, (float) constVertex.y);
            interpolate(secondVertex, quad, result);
            emitter.interpolate(2, quad, result.v0, result.v1, result.v2, result.v3);
            emitter.pos(2, (float) constVertex.x, (float) constVertex.y);
            emitter.interpolate(3, quad, result.v0, result.v1, result.v2, result.v3);
            emitter.pos(3, (float) constVertex.x, (float) constVertex.y);
            emitter.emit();
        }
    }

    private static void interpolate(final Vec2d target, final GuiQuad quad, final InterpResult result) {
        final double x0, y0, x1, y1, x2, y2;
        final int side = side(result.firstDiagonal, result.secondDiagonal, target);
        x0 = quad.x(0);
        y0 = quad.y(0);
        x2 = quad.x(2);
        y2 = quad.y(2);
        if (side >= 0) {
            x1 = quad.x(1);
            y1 = quad.y(1);
        } else {
            x1 = quad.x(3);
            y1 = quad.y(3);
        }
        final double det = (y1 - y2) * (x0 - x2) + (x2 - x1) * (y0 - y2);
        final double gamma0 = ((y1 - y2) * (target.x - x2) + (x2 - x1) * (target.y - y2)) / det;
        final double gamma1 = ((y2 - y0) * (target.x - x2) + (x0 - x2) * (target.y - y2)) / det;
        final double gamma2 = 1 - gamma0 - gamma1;
        result.v0 = gamma0;
        result.v2 = gamma2;
        if (side >= 0) {
            result.v1 = gamma1;
            result.v3 = 0;
        } else {
            result.v1 = 0;
            result.v3 = gamma1;
        }
    }

    private static void edgeClip(final List<Vec2d> polygon, final Vec2d p0, final Vec2d p1, final int polyWinding, final List<Vec2d> result) {
        Vec2d prev = polygon.get(polygon.size() - 1);
        int prevWinding = side(p0, p1, prev);
        result.clear();
        if (prevWinding != -polyWinding) {
            result.add(prev);
        }
        final int size = polygon.size();
        for (int i = 0; i < size; i++) {
            final Vec2d cur = polygon.get(i);
            final int winding = side(p0, p1, cur);
            if (prevWinding + winding == 0 && prevWinding != 0) {
                final Vec2d intersection = lineIntersection(p0, p1, prev, cur);
                if (intersection != null) {
                    result.add(intersection);
                }
            }
            if (i != size - 1) {
                if (winding != -polyWinding) {
                    result.add(cur);
                }
                prev = cur;
                prevWinding = winding;
            }
        }
    }

    private static List<Vec2d> quadClip(final GuiQuad toClip, final double x0, final double y0, final double x1, final double y1) {
        final List<Vec2d> polygon = new ArrayList<>(4);
        for (int i = 0; i < 4; i++) {
            polygon.add(new Vec2d(toClip.x(i), toClip.y(i)));
        }
        final List<Vec2d> clipper = new ArrayList<>();
        clipper.add(new Vec2d(x0, y0));
        clipper.add(new Vec2d(x1, y0));
        clipper.add(new Vec2d(x1, y1));
        clipper.add(new Vec2d(x0, y1));
        List<Vec2d> result0 = new ArrayList<>(8);
        List<Vec2d> result1 = new ArrayList<>(8);
        edgeClip(polygon, clipper.get(3), clipper.get(0), 1, result1);
        for (int i = 0; i < 3; i++) {
            final List<Vec2d> swap = result1;
            result1 = result0;
            result0 = swap;
            if (result0.size() == 0) {
                result1.clear();
                break;
            }
            edgeClip(result0, clipper.get(i), clipper.get(i + 1), 1, result1);
        }
        return result1;
    }

    private static Vec2d lineIntersection(final Vec2d p0, final Vec2d p1, final Vec2d q0, final Vec2d q1) {
        final Vec2d dP = p1.subtract(p0);
        final Vec2d dQ = q1.subtract(q0);
        double dCross = cross(dQ, dP);
        if (dCross == 0) {
            return null;
        }
        final Vec2d delta = p0.subtract(q0);
        dCross = cross(delta, dP) / dCross;
        if (dCross <= 0 || dCross >= 1) {
            return null;
        }
        return new Vec2d(q0.x + dCross * dQ.x, q0.y + dCross * dQ.y);
    }

    private static double cross(final Vec2d first, final Vec2d second) {
        return first.x * second.y - second.x * first.y;
    }

    private static int side(final Vec2d firstPoint, final Vec2d secondPoint, final Vec2d targetPoint) {
        final double cross = cross(secondPoint.subtract(firstPoint), targetPoint.subtract(secondPoint));
        return cross == 0 ? 0 : cross > 0 ? 1 : -1;
    }

    private static final class InterpResult {
        public final Vec2d firstDiagonal, secondDiagonal;
        public double v0, v1, v2, v3;

        private InterpResult(final Vec2d firstDiagonal, final Vec2d secondDiagonal) {
            this.firstDiagonal = firstDiagonal;
            this.secondDiagonal = secondDiagonal;
        }
    }
}

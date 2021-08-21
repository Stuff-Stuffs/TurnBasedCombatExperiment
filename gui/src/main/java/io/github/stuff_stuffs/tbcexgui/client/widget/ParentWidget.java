package io.github.stuff_stuffs.tbcexgui.client.widget;

import io.github.stuff_stuffs.tbcexutil.common.IdSupplier;

public interface ParentWidget extends Widget {
    IdSupplier ID_SUPPLIER = new IdSupplier();

    WidgetHandle addWidget(Widget widget);

    void removeWidget(WidgetHandle handle);
}

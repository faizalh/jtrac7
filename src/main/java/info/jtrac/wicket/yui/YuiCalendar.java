/*
 * Copyright 2002-2005 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package info.jtrac.wicket.yui;

import info.jtrac.wicket.ErrorHighlighter;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptContentHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.convert.ConversionException;
import org.apache.wicket.util.convert.IConverter;
import org.apache.wicket.util.convert.converter.AbstractConverter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * yui date picker panel
 */
public class YuiCalendar extends FormComponentPanel {

    private TextField dateField;
    private WebMarkupContainer container;

    private OnDomReadyHeaderItem onDomReadyJavascript;
    private JavaScriptContentHeaderItem javaScriptItem;

    public YuiCalendar(String id, IModel model, boolean required) {

        super(id, null);        
        /*
        add(HeaderContributor.forJavaScript("resources/yui/yahoo/yahoo-min.js"));
        add(HeaderContributor.forJavaScript("resources/yui/event/event-min.js"));
        add(HeaderContributor.forJavaScript("resources/yui/dom/dom-min.js"));        
        add(HeaderContributor.forJavaScript("resources/yui/calendar/calendar-min.js"));
        add(HeaderContributor.forJavaScript("resources/yui/calendar/calendar-utils.js"));
        add(HeaderContributor.forCss("resources/yui/calendar/assets/calendar.css"));         
        */
        initHeaderItems();

        dateField = new TextField("field", model, Date.class) {
            @Override
            public IConverter getConverter(Class clazz) {
                return new AbstractConverter() {
                    private DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

                    public Object convertToObject(String s, Locale locale) {
                        if (s == null || s.trim().length() == 0) {
                            return null;
                        }
                        try {
                            return df.parse(s);
                        } catch (Exception e) {
                            throw new ConversionException(e);
                        }
                    }

                    protected Class getTargetType() {
                        return Date.class;
                    }

                    @Override
                    public String convertToString(Object o, Locale locale) {
                        Date d = (Date) o;
                        return df.format(d);
                    }
                };
            }

            @Override
            public IModel getLabel() {
                return YuiCalendar.this.getLabel();
            }
        };
        dateField.setOutputMarkupId(true);
        dateField.setRequired(required);
        dateField.add(new ErrorHighlighter());
        add(dateField);

        final WebMarkupContainer button = new WebMarkupContainer("button");
        button.setOutputMarkupId(true);
        button.add(new AttributeModifier("onclick", new AbstractReadOnlyModel() {
            public Object getObject() {
                return "showCalendar(" + getCalendarId() + ", '" + getInputId() + "');";
            }
        }));
        add(button);

        container = new WebMarkupContainer("container");
        container.setOutputMarkupId(true);
        add(container);
    }

    @Override
    public void updateModel() {
        dateField.updateModel();
    }

    private String getCalendarId() {
        return getMarkupId();
    }

    private String getInputId() {
        return dateField.getMarkupId();
    }

    private String getContainerId() {
        return container.getMarkupId();
    }

    public void renderHead(IHeaderResponse response) {
        String calendarId = getCalendarId();
        response.render(onDomReadyJavascript);
        response.render(javaScriptItem);
    }

    private void initHeaderItems() {
        String calendarId = getCalendarId();
        onDomReadyJavascript = new OnDomReadyHeaderItem("init" + calendarId + "()");
        javaScriptItem = new JavaScriptContentHeaderItem(
                "function init" + calendarId + "() { "
                        + calendarId + " = new YAHOO.widget.Calendar('" + calendarId + "', '" + getContainerId() + "'); "
                        + calendarId + ".selectEvent.subscribe(handleSelect, [ " + calendarId + ", '" + getInputId() + "' ], true); }",
                calendarId,
                "IE 7");
    }
}

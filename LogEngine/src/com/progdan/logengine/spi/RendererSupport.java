

package com.progdan.logengine.spi;

import com.progdan.logengine.*;
import com.progdan.logengine.or.RendererMap;
import com.progdan.logengine.or.ObjectRenderer;


public interface RendererSupport {

  public
  RendererMap getRendererMap();

  public
  void setRenderer(Class renderedClass, ObjectRenderer renderer);

}

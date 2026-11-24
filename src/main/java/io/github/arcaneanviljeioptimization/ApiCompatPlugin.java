package io.github.arcaneanviljeioptimization;
import java.util.*;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.*;
public final class ApiCompatPlugin implements IMixinConfigPlugin {
 public void onLoad(String p) {}
 public String getRefMapperConfig(){return null;}
 public boolean shouldApplyMixin(String target,String mixin) {
  boolean ok;
  try { ok=ApiContract.supported(ApiChecks.LIVE,target,mixin); }
  catch(Exception | LinkageError e) { ok=false; org.slf4j.LoggerFactory.getLogger("arcane-anvil-jei-optimization").warn("API inspection failed for {}",mixin,e); }
  org.slf4j.LoggerFactory.getLogger("arcane-anvil-jei-optimization").info("API contract {}: {}",mixin,ok?"ENABLED":"DISABLED (incompatible structure)");
  return ok;
 }
 public void acceptTargets(Set<String> mine,Set<String> others){}
 public List<String> getMixins(){return null;}
 public void preApply(String t,ClassNode n,String m,IMixinInfo i){}
 public void postApply(String t,ClassNode n,String m,IMixinInfo i){}
}

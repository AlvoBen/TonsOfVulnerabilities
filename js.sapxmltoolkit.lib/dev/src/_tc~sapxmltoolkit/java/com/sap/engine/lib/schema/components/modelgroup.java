package com.sap.engine.lib.schema.components;

import java.util.Vector;

/**
 * Represents a model group component.
 *
 * Excerpt:
 * <pre>
 *   Schema Component: Model Group
 *     {compositor}          One of all, choice or sequence.
 *     {particles}           A list of particles
 *     {annotation}          Optional. An annotation.
 * </pre>
 *
 * @author       Nick Nickolov, nick_nickolov@abv.bg
 * @version      November 2001
 */
public interface ModelGroup
  extends Base {

  /**
   * Returns true if and only if the {compositor} is 'all'
   */
  boolean isCompositorAll();


  /**
   * Returns true if and only if the {compositor} is 'choice'
   */
  boolean isCompositorChoice();


  /**
   * Returns true if and only if the {compositor} is 'sequence'
   */
  boolean isCompositorSequence();

  Vector getParticles();
  
  /**
   * Appends to the (non-null) Vector
   * all {particles} of this component.
   */
  void getParticles(java.util.Vector v);


  /**
   *
   */
  Particle[] getParticlesArray();
  
  int getMinimumEffectiveTotalRange();
  
  int getMaximumEffectiveTotalRange();
  
  boolean isUnboundedMaximumEffectiveTotalRange();
}


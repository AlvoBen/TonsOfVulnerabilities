import { LightningElement } from 'lwc';
import Icons from "@salesforce/resourceUrl/CommunityIcons_SR_HUM";

export default class dropDownFooter_LWCCommunity_HUM extends LightningElement {
  hasRendered = false;
  expandIconUrl = Icons + "/humanaIcons/caret-down@3x.png";
  collapseIconUrl = Icons + "/humanaIcons/arrow-up@3x.png";

  /**
   * @function - renderedCallback
   * @description - This method is used to show the accordion block.
   */
  renderedCallback() {
    try {
      const accordion = this.template.querySelector(".accordionButton");
      const accordionIcon = this.template.querySelector(".accordionIcon");
      const collapseUrl = this.collapseIconUrl;
      const expandUrl = this.expandIconUrl;
      if (!this.hasRendered) {
        accordion.addEventListener("click", function() {
          const panel = this.nextElementSibling;
          if (panel.style.maxHeight) {
            panel.style.maxHeight = null;
            accordionIcon.src = expandUrl;
          } else {
            panel.style.maxHeight = panel.scrollHeight + "px";
            accordionIcon.src = collapseUrl;
          }
        });
      }
      this.hasRendered = true;
    } catch (error) {
      console.error("Error in  renderedCallback" + error);
    }
  }
}
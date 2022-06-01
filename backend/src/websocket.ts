var wb: any;

function getWS(): any {
  return wb;
}

function setWS(pwb: any): any {
  wb = pwb;
}

export default {
  getWS,
  setWS,
} as const;

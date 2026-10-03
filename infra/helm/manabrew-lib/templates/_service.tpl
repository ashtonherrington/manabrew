{{/*
Standard ClusterIP Service in front of manabrew-lib.deployment.
Usage in a service chart: {{ include "manabrew-lib.service" . }}
*/}}
{{- define "manabrew-lib.service" -}}
apiVersion: v1
kind: Service
metadata:
  name: {{ include "manabrew-lib.fullname" . }}
  labels:
    {{- include "manabrew-lib.labels" . | nindent 4 }}
spec:
  type: ClusterIP
  selector:
    {{- include "manabrew-lib.selectorLabels" . | nindent 4 }}
  ports:
    - name: http
      port: {{ .Values.servicePort | default 80 }}
      targetPort: http
{{- end -}}

import { apiClient } from './client';
import { ApiResponse, ApplicationDocument } from '../types';

export async function fetchApplicationDocuments(
  applicationId: number
): Promise<ApplicationDocument[]> {
  const response = await apiClient.get<ApiResponse<ApplicationDocument[]>>(
    `/applications/${applicationId}/documents`
  );
  return response.data.data;
}

export async function downloadApplicationDocument(
  applicationId: number,
  documentId: number,
  preferredFileName?: string
): Promise<void> {
  const response = await apiClient.get<Blob>(
    `/applications/${applicationId}/documents/${documentId}/download`,
    {
      responseType: 'blob',
    }
  );

  const blobUrl = window.URL.createObjectURL(new Blob([response.data]));
  const link = document.createElement('a');
  link.href = blobUrl;
  link.setAttribute('download', preferredFileName || `dokumen_${applicationId}_${documentId}`);
  document.body.appendChild(link);
  link.click();
  link.parentNode?.removeChild(link);
  window.URL.revokeObjectURL(blobUrl);
}

﻿using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Linq;
using System.IO;
using System.Net;
using System.Runtime.Serialization;
using System.Text;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Navigation;
using System.Windows.Shapes;
using ESRI.ArcGIS.Client;
using ESRI.ArcGIS.Client.Toolkit;
using ESRI.ArcGIS.Client.Bing;
using ESRI.ArcGIS.Client.Geometry;
using ESRI.ArcGIS.Client.Symbols;
using ESRI.ArcGIS.Client.Tasks;
using Epi;
using Epi.Data;
using EpiDashboard.Mapping.ShapeFileReader;
using System.Windows.Controls.DataVisualization.Charting;

namespace EpiDashboard.Mapping
{

    public class ShapeLayerProvider : ILayerProvider
    {
        private Map myMap;
        private Guid layerId;
        private string fileName;

        public ShapeLayerProvider(Map myMap)
        {
            this.myMap = myMap;
            this.layerId = Guid.NewGuid();
        }

        public void Refresh()
        {
            GraphicsLayer markerLayer = myMap.Layers[layerId.ToString()] as GraphicsLayer;
            if (markerLayer != null)
            {
                markerLayer.ClearGraphics();
                RenderShape(this.fileName);
            }
        }

        public void MoveUp()
        {
            Layer layer = myMap.Layers[layerId.ToString()];
            int currentIndex = myMap.Layers.IndexOf(layer);
            if (currentIndex < myMap.Layers.Count - 1)
            {
                myMap.Layers.Remove(layer);
                myMap.Layers.Insert(currentIndex + 1, layer);
            }
        }

        public void MoveDown()
        {
            Layer layer = myMap.Layers[layerId.ToString()];
            int currentIndex = myMap.Layers.IndexOf(layer);
            if (currentIndex > 1)
            {
                myMap.Layers.Remove(layer);
                myMap.Layers.Insert(currentIndex - 1, layer);
            }
        }

        public void RenderShape(string fileName)
        {
            this.fileName = fileName;

            GraphicsLayer shapeLayer = myMap.Layers[layerId.ToString()] as GraphicsLayer;
            if (shapeLayer != null)
            {
                myMap.Layers.Remove(shapeLayer);
            }
            shapeLayer = new GraphicsLayer();
            shapeLayer.ID = layerId.ToString();
            myMap.Layers.Add(shapeLayer);

            //Get the file info objects for the SHP and the DBF file selected by the user
            FileInfo shapeFile = new FileInfo(fileName);
            FileInfo dbfFile = new FileInfo(fileName.ToLower().Replace(".shp", ".dbf"));
            if (!dbfFile.Exists)
            {
                System.Windows.MessageBox.Show("Associated DBF file not found");
                return;
            }

            //Read the SHP and DBF files into the ShapeFileReader
            ShapeFileReader.ShapeFile shapeFileReader = new ShapeFileReader.ShapeFile();
            if (shapeFile != null && dbfFile != null)
            {
                shapeFileReader.Read(shapeFile, dbfFile);
            }
            else
            {
                System.Windows.MessageBox.Show("Associated DBF file not found");
                return;
            }

            int recCount = shapeFileReader.Records.Count;
            int rgbFactor = 255 / recCount;
            int counter = 0;
            foreach (ShapeFileReader.ShapeFileRecord record in shapeFileReader.Records)
            {
                Graphic graphic = record.ToGraphic();
                if (graphic != null)
                {
                    graphic.Symbol = GetFillSymbol(new SolidColorBrush(Color.FromArgb(192, 255, 255, 255)));
                    shapeLayer.Graphics.Add(graphic);
                }
                counter += rgbFactor;
            }
            Envelope shapeFileExtent = shapeFileReader.GetExtent();
            if (shapeFileExtent.SpatialReference == null)
            {
                myMap.Extent = shapeFileExtent;
            }
            else
            {
                if (shapeFileExtent.SpatialReference.WKID == 4326)
                {
                    myMap.Extent = new Envelope(ESRI.ArcGIS.Client.Bing.Transform.GeographicToWebMercator(new MapPoint(shapeFileExtent.XMin, shapeFileExtent.YMin)), ESRI.ArcGIS.Client.Bing.Transform.GeographicToWebMercator(new MapPoint(shapeFileExtent.XMax, shapeFileExtent.YMax)));
                }
            }
        }

        public string RenderShape()
        {
            Guid layerId = Guid.NewGuid();
            //Create the dialog allowing the user to select the "*.shp" and the "*.dbf" files
            Microsoft.Win32.OpenFileDialog ofd = new Microsoft.Win32.OpenFileDialog();
            ofd.Filter = "ESRI Shapefiles (*.shp)|*.shp";
            //ofd.Multiselect = true;

            if (ofd.ShowDialog().Value)
            {
                RenderShape(ofd.FileName);
                return ofd.FileName;
            }
            return null;
        }

        public SimpleFillSymbol GetFillSymbol(SolidColorBrush brush)
        {
            SimpleFillSymbol symbol = new SimpleFillSymbol();
            symbol.Fill = brush;
            symbol.BorderBrush = new SolidColorBrush(Colors.Gray);
            symbol.BorderThickness = 1;
            return symbol;
        }

        #region ILayerProvider Members

        public void CloseLayer()
        {
            GraphicsLayer graphicsLayer = myMap.Layers[layerId.ToString()] as GraphicsLayer;
            if (graphicsLayer != null)
            {
                myMap.Layers.Remove(graphicsLayer);
            }
        }

        #endregion
    }
}
